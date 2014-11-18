package objenome.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import objenome.ConfigurableBuilder;
import objenome.Context;
import objenome.ProtoContext;
import objenome.util.FindConstructor;
import objenome.util.FindMethod;
import objenome.util.InjectionUtils;


/**
 * The implementation of the Configurable Factory.
 *
 * @author sergio.oliveira.jr@gmail.com
 */
public class ClassBuilder implements ConfigurableBuilder {

    private final ProtoContext container;

    private final Class<?> klass;

    private Map<String, Object> props = null;

    private List<Object> initValues = null;

    private List<Class<?>> initTypes = null;

    private Constructor<?> constructor = null;

    private Map<String, Method> cache = null;

    private boolean useZeroArgumentsConstructor = false;

    public final Set<ConstructorDependency> constructorDependencies;
    private LinkedList<Parameter> initPrimitives;

    public ClassBuilder(ProtoContext container, Class<?> klass) {

        this(container, klass, null);
    }

    public ClassBuilder(ProtoContext container, Class<?> klass, Set<ConstructorDependency> constructorDependencies) {

        this.container = container;

        this.klass = klass;

        this.constructorDependencies = constructorDependencies;

    }

    @Override
    public ConfigurableBuilder addPropertyValue(String name, Object value) {

        if (props == null) {

            props = new HashMap<String, Object>();

            cache = new HashMap<String, Method>();
        }

        props.put(name, value);

        return this;
    }

    public List<Class<?>> getInitTypes() {
        return initTypes;
    }

    public List<Object> getInitValues() {
        return initValues;
    }

    public List<Parameter> getInitPrimitives() {
        return initPrimitives;
    }
    

    @Override
    public ConfigurableBuilder useZeroArgumentConstructor() {

        this.useZeroArgumentsConstructor = true;

        return this;
    }

    @Override
    public ConfigurableBuilder addPropertyDependency(String property, Object key) {

        String k = InjectionUtils.getKeyName(key);

        return addPropertyValue(property, new DependencyKey(k));
    }

    @Override
    public ConfigurableBuilder addPropertyDependency(String property) {

        return addPropertyDependency(property, property);
    }

    @Override
    public ConfigurableBuilder constructorUse(Object key) {

        String k = InjectionUtils.getKeyName(key);

        Class<?> t = container.type(k);
        
        return addInitValue(new DependencyKey(k), t);
    }

    private ConfigurableBuilder addInitValue(Object value, Class<?> type) {

        if (initValues == null) {

            initValues = new LinkedList<Object>();

            initTypes = new LinkedList<Class<?>>();
        }

        initValues.add(value);

        initTypes.add(type);

        return this;
    }

    @Override
    public ConfigurableBuilder addInitValue(Object value) {

        return addInitValue(value, value.getClass());
    }

    @Override
    public ConfigurableBuilder addInitPrimitive(Object value) {

        Class<?> primitive = getPrimitiveFrom(value);

        if (primitive == null) {
            throw new IllegalArgumentException("Value is not a primitive: " + value);
        }

        return addInitValue(value, primitive);
    }

    private List<Class<?>> convertToPrimitives(List<Class<?>> list) {

        if (list == null) {
            return null;
        }

        Iterator<Class<?>> iter = list.iterator();

        List<Class<?>> results = new LinkedList<Class<?>>();

        while (iter.hasNext()) {

            Class<?> klass = iter.next();

            Class<?> primitive = getPrimitiveFrom(klass);

            if (primitive != null) {

                results.add(primitive);

            } else {

                results.add(klass);
            }
        }

        return results;
    }

    private static Class<?>[] getClasses(List<Class<?>> values) {

        if (values == null) {
            return new Class[0];
        }

        Class<?>[] types = new Class[values.size()];

        return values.toArray(types);
    }

    private static Object[] getValues(Context context, List<Object> values) throws InstantiationException {

        if (values == null) {
            return null;
        }

        Object[] array = new Object[values.size()];

        int index = 0;

        Iterator<Object> iter = values.iterator();

        while (iter.hasNext()) {

            Object obj = iter.next();

            if (obj instanceof DependencyKey) {

                DependencyKey dk = (DependencyKey) obj;

                array[index++] = context.get(dk.getKey());

            } else {

                array[index++] = obj;
            }
        }

        return array;
    }

    /*
     * Use reflection to set a property in the bean
     */
    private void setValue(Object bean, String name, Object value) {

        try {

            StringBuilder sb = new StringBuilder(30);
            sb.append("set");
            sb.append(name.substring(0, 1).toUpperCase());

            if (name.length() > 1) {
                sb.append(name.substring(1));
            }

            String methodName = sb.toString();

            if (!cache.containsKey(name)) {

                Method m = null;

                try {

                    m = FindMethod.getMethod(klass, methodName, new Class[]{value.getClass()});

                } catch (Exception e) {

                    // try primitive...
                    Class<?> primitive = getPrimitiveFrom(value);

                    if (primitive != null) {

                        try {

                            m = klass.getMethod(methodName, new Class[]{primitive});

                        } catch (Exception ex) {
                            // not found!
                        }
                    }

                    if (m == null) {

                        throw new InstantiationException("Cannot find method for property: " + name);
                    }
                }

                if (m != null) {

                    cache.put(name, m);

                    m.setAccessible(true);
                }
            }

            Method m = cache.get(name);

            if (m != null) {

                m.invoke(bean, new Object[]{value});
            }

        } catch (Exception e) {

            throw new RuntimeException("Error trying to set a property with reflection: " + name, e);
        }
    }

    private static Class<?> getPrimitiveFrom(Object w) {
        if (w instanceof Boolean) {
            return Boolean.TYPE;
        } else if (w instanceof Byte) {
            return Byte.TYPE;
        } else if (w instanceof Short) {
            return Short.TYPE;
        } else if (w instanceof Character) {
            return Character.TYPE;
        } else if (w instanceof Integer) {
            return Integer.TYPE;
        } else if (w instanceof Long) {
            return Long.TYPE;
        } else if (w instanceof Float) {
            return Float.TYPE;
        } else if (w instanceof Double) {
            return Double.TYPE;
        }
        return null;
    }

    private static Class<?> getPrimitiveFrom(Class<?> klass) {
        if (klass.equals(Boolean.class)) {
            return Boolean.TYPE;
        } else if (klass.equals(Byte.class)) {
            return Byte.TYPE;
        } else if (klass.equals(Short.class)) {
            return Short.TYPE;
        } else if (klass.equals(Character.class)) {
            return Character.TYPE;
        } else if (klass.equals(Integer.class)) {
            return Integer.TYPE;
        } else if (klass.equals(Long.class)) {
            return Long.TYPE;
        } else if (klass.equals(Float.class)) {
            return Float.TYPE;
        } else if (klass.equals(Double.class)) {
            return Double.TYPE;
        }
        return null;
    }

    @Override
    public <T> T instance(Context context) {

        Object obj = null;

        Object[] values = null;

        synchronized (this) {

            if (constructor == null) {

                if (!useZeroArgumentsConstructor) {

                    updateConstructorDependencies();

                } else {

                    if (initTypes != null) {
                        initTypes = null; // just in case client did something stupid...
                    }
                    if (initValues != null) {
                        initValues = null; // just in case client did something stupid...
                    }
                }

                try {

                    //constructor = klass.getConstructor(getClasses(initTypes));
                    constructor = FindConstructor.getConstructor(klass, getClasses(initTypes));

                } catch (Exception e) {

                    // try primitives...
                    try {

                        //constructor = klass.getConstructor(getClasses(convertToPrimitives(initTypes)));
                        constructor = FindConstructor.getConstructor(klass, getClasses(convertToPrimitives(initTypes)));

                    } catch (Exception ee) {

                        throw new RuntimeException("Cannot find a constructor for class: " + klass);
                    }
                }
            }

            try {

                values = getValues(context, initValues);

            } catch (Exception e) {

                new RuntimeException("Cannot instantiate values for constructor: " + e.toString(), e);
            }
        }

        try {

            obj = constructor.newInstance(values);

        } catch (Exception e) {

            throw new RuntimeException("Cannot create instance from constructor: " + constructor + ": " + e.toString() + " with values=" + Arrays.toString(values), e);
        }

        if (props != null && props.size() > 0) {

            Iterator<String> iter = props.keySet().iterator();

            while (iter.hasNext()) {

                String name = iter.next();

                Object value = props.get(name);

                if (value instanceof DependencyKey) {

                    DependencyKey dk = (DependencyKey) value;

                    value = context.get(dk.getKey());
                }

                setValue(obj, name, value);
            }
        }

        return (T) obj;
    }

    private static boolean betterIsAssignableFrom(Class<?> klass1, Class<?> klass2) {

        // with autoboxing both ways...
        if (klass1.isAssignableFrom(klass2)) {
            return true;
        }

        Class<?> k1 = klass1.isPrimitive() ? klass1 : getPrimitiveFrom(klass1);
        Class<?> k2 = klass2.isPrimitive() ? klass2 : getPrimitiveFrom(klass2);

        if (k1 == null || k2 == null) {
            return false;
        }

        return k1.isAssignableFrom(k2);
    }

    public void updateConstructorDependencies() {
        updateConstructorDependencies(true);
    }
    
    public void updateConstructorDependencies(boolean requirePrimitives) {

        Constructor<?>[] constructors = klass.getConstructors();

        for (Constructor<?> c : constructors) {

            LinkedList<Class<?>> providedInitTypes = null;

            if (initTypes != null) {

                providedInitTypes = new LinkedList<Class<?>>(initTypes);

            } else {

                providedInitTypes = new LinkedList<Class<?>>();
            }

            LinkedList<Object> providedInitValues = null;

            if (initValues != null) {

                providedInitValues = new LinkedList<Object>(initValues);

            } else {

                providedInitValues = new LinkedList<Object>();
            }

            LinkedList<Class<?>> newInitTypes = new LinkedList<Class<?>>();
            LinkedList<Object> newInitValues = new LinkedList<Object>();
            LinkedList<Parameter> newInitPrimitives = new LinkedList<>();

            Set<ConstructorDependency> constructorDependencies = this.constructorDependencies != null ? this.constructorDependencies : container.getConstructorDependencies();

            Set<ConstructorDependency> dependencies = new HashSet<ConstructorDependency>(constructorDependencies);

                        
            Parameter[] constructorParams = c.getParameters();

            if (constructorParams == null || constructorParams.length == 0) {
                //Default constructor
                this.initTypes = newInitTypes; //use empty lists to indicate this
                this.initValues = newInitValues;
                this.initPrimitives = newInitPrimitives;
                continue; 
            }
            for (final Parameter p : constructorParams) {
                Class<?> pc = p.getType();

                // first see if it was provided...
                Class<?> provided = providedInitTypes.isEmpty() ? null : providedInitTypes.get(0);

                if (provided != null && pc.isAssignableFrom(provided)) {

                    // matched this one, so remove...
                    
                    newInitTypes.add(providedInitTypes.removeFirst()); 

                    newInitValues.add(providedInitValues.removeFirst());

                    continue;

                } else {

                    // contains auto-wiring...
                    Iterator<ConstructorDependency> iter = dependencies.iterator();

                    boolean foundMatch = false;

                    while (iter.hasNext()) {

                        ConstructorDependency d = iter.next();

                        if (betterIsAssignableFrom(pc, d.getSourceType())) {

                            iter.remove();

                            newInitTypes.add(d.getSourceType());

                            newInitValues.add(new DependencyKey(p, d.getSource()));

                            foundMatch = true;

                            break;
                        }
                    }

                    if (foundMatch) {
                        continue; // next constructor param...
                    }

                    
                }
                
                //record primitives in constructor
                if (pc.equals(double.class) || (pc.equals(int.class))) {
                    newInitPrimitives.add(p);
                }
                else {
                    //System.out.println("Missing: " + p + " " + pc.getName());
                }

                break; // no param... next constructor...
            }

            // done, contains if found...
            int capableSize = requirePrimitives ? 
                newInitTypes.size() : (newInitPrimitives.size() + newInitTypes.size());
                    
            if (constructorParams.length == capableSize && providedInitTypes.isEmpty()) {

                this.initTypes = newInitTypes;
                this.initPrimitives = newInitPrimitives;
                this.initValues = newInitValues;
            }
        }
        
        //return missing;
    }

    public final static class DependencyKey {

        public final String key;
        public final Parameter param;

        public DependencyKey(String key) {
            this(null, key);            
        }
        
        public DependencyKey(Parameter param, String key) {
            this.param = param;
            this.key = key;
        }

        private String getKey() {
            return key;
        }

        @Override
        public String toString() {
            if (param!=null)
                return param + " (" + key + ")";
            else
                return key;
        }
        
        
    }

    @Override
    public Class<?> type() {
        return klass;
    }

    @Override
    public String toString() {
        return "ClassBuilder[" + type() + ']';
    }
    
    
}
