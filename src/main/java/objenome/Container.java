package objenome;

import objenome.solution.dependency.*;
import objenome.util.InjectionUtils;
import objenome.util.InjectionUtils.Provider;
import objenome.util.bean.BeanProxyBuilder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The deterministic implementation of of IoC container.
 *
 * @author sergio.oliveira.jr@gmail.com
 */
public class Container extends AbstractPrototainer implements AbstractContainer {


    private final Map<String, Object> singletonsCache;

    private final Map<String, ThreadLocal<Object>> threadLocalsCache;

    
    
    public Container() {
        this(false);        
    }
    
    public Container(boolean concurrent) {
        super(concurrent);
        singletonsCache = concurrent ? new ConcurrentHashMap() : new HashMap();
        threadLocalsCache = concurrent ? new ConcurrentHashMap() : new HashMap();        
    }

    public Container(final AbstractPrototainer parent) {
        super(
                //TODO clone according to concurrent implementation:                
                new HashMap(parent.builders), 
                new HashMap(parent.scopes), 
                new HashSet(parent.setterDependencies), 
                new HashSet(parent.constructorDependencies), 
                new HashSet(parent.forConstructMethod));
        
        singletonsCache = parent.concurrent ? new ConcurrentHashMap() : new HashMap();
        threadLocalsCache = parent.concurrent ? new ConcurrentHashMap() : new HashMap();
    }
    
    
    
    
    public <T> T get(Object key, T defaultValue) {
        T existing = get(key);
        if (existing == null)
            return defaultValue;
        return existing;
    }
    

    @Override
    public <T> T get(Object key) {

        String name = InjectionUtils.getKeyName(key);

        Builder c = builders.get(name);
        if (c == null) {
            if (key instanceof Class)
                return (T) get((Class)key);
            return null;
        }


        Scope scope = scopes.get(name);

        Object target = null;

        try {

            if (scope == Scope.SINGLETON) {

                boolean needsToCreate;

                synchronized (this) {

                    target = singletonsCache.get(name);
                    if (target!=null) return (T) target; // no need to wire again...
                    else needsToCreate = true;
                }

                if (needsToCreate) {

                    // instance needs to be in a non-synchronized block
                    target = c.instance(this);

                    checkInterceptable(c, target);

                    synchronized (this) {

                        singletonsCache.put(name, target);
                    }
                }

            } else if (scope == Scope.THREAD) {

                boolean needsToCreate;

                boolean needsToAddToCache = false;

                ThreadLocal<Object> t;

                synchronized (this) {

                    t = threadLocalsCache.get(name);
                    if (t!=null) {

                        target = t.get();

                        // different thread...
                        // don't return... let it be wired...
                        if (target == null) needsToCreate = true;
                        else return (T) target; // no need to wire again...

                    } else {

                        t = new ThreadLocal<>();

                        needsToCreate = true;

                        needsToAddToCache = true;

                        // let it be wired...
                    }
                }

                if (needsToCreate) {

                    // instance needs to be in a non-synchronized block
                    target = c.instance(this);

                    checkInterceptable(c, target);

                    t.set(target);
                }

                if (needsToAddToCache) synchronized (this) {

                    threadLocalsCache.put(name, t);
                }

            } else if (scope == Scope.NONE) {

                target = c.instance(this);

                checkInterceptable(c, target);

            } else throw new UnsupportedOperationException("Don't know how to handle scope: " + scope);

            if (target != null) for (SetterDependency d : setterDependencies) {

                // has dependency ?
                Method m = d.check(target.getClass());

                if (m != null) {

                    String sourceKey = d.getSource();

                    // cannot depend on itself... also avoid recursive StackOverflow...
                    if (sourceKey.equals(name)) continue;

                    Object source = get(sourceKey);

                    try {

                        // apply
                        m.invoke(target, source);

                    } catch (Exception e) {

                        throw new RuntimeException("Cannot inject dependency: method = " + (m != null ? m.getName() : "NULL") + " / source = "
                                + (source != null ? source : "NULL") + " / target = " + target, e);

                    }
                }
            }

            return (T) target; // return target nicely with all of dependencies

        } catch (RuntimeException e) {

            throw new RuntimeException(e);
        }
    }

    private void checkInterceptable(Builder f, Object value) {
        if (f instanceof Interceptor)
            ((Interceptor) f).onCreated(value);
    }

    
    public <T> T the(Object key, Builder builder) {        
        if (builder!=null)
            usable(key, Scope.SINGLETON, builder);
        return get(key);
    }    
    public <T> T the(final Class<? extends T> c) {        
        T existing = get((Object)c);
        return existing == null ? the(c, new ClassBuilder(this, c).instance(this)) : existing;
    }
    public <T> T the(Object key, Object value) {        
        return the(key, new SingletonBuilder(value));
    }

//    public <T,X> T the(Object key, X... value) {
//        return the(key, new SingletonBuilder(value));
//    }

    public <T> T the(Object value) {    
        T existing = get(value);
        return existing == null ? the(value.getClass(), new SingletonBuilder(value)) : existing;
    }

    public Map<String, Object> getSingletons() {
        return singletonsCache;
    }


    
    @Override
    public <T> T get(final Class<? extends T> c) {
        //if c is actually a key and not an arbitrary class this container has never been told about:
        T b = get(InjectionUtils.getKeyName(c));
        return b != null ? b : getClassBuilder(c).instance(this);
    }

    @Override
    public <C> C apply(C instance) {

        Provider p = new Provider() {

            @Override
            public Object get(String key) {

                return Container.this.get(key);
            }

            @Override
            public boolean hasValue(String key) {

                return Container.this.contains(key);
            }

        };

        try {

            InjectionUtils.getObject(instance, p, false, null, true, false, true);

        } catch (Exception e) {

            throw new RuntimeException("Error populating bean: " + instance, e);
        }
        
        return instance;
    }

    @Override
    public Builder usable(Object key, Scope scope, Builder factory) {
        Builder b = super.usable(key, scope, factory);

        //singletonsCache.remove(keyString); // just in case we are overriding a previous singleton bean...
        ThreadLocal<Object> threadLocal = threadLocalsCache.remove(InjectionUtils.getKeyName(key)); // just in case we are overriding a previous thread local...
        if (threadLocal != null) threadLocal.remove();

        return b;
    }

    
    @Override
    public void remove(Scope scope) {
        if (scope == Scope.SINGLETON) {
            List<ClearableHolder> listToClear = new LinkedList<>();
            synchronized (this) {
                for (Map.Entry<String, Object> entry : singletonsCache.entrySet()) {
                    Builder factory = builders.get(entry.getKey());
                    if (factory instanceof Interceptor)
                        listToClear.add(new ClearableHolder((Interceptor) factory, entry.getValue()));
                }
                singletonsCache.clear();
            }
            // remove everything inside a non-synchronized block...
            for (ClearableHolder cp : listToClear) cp.clear();
        } else if (scope == Scope.THREAD) {
            List<ClearableHolder> listToClear = new LinkedList<>();
            synchronized (this) {
                for (Map.Entry<String, ThreadLocal<Object>> entry : threadLocalsCache.entrySet()) {
                    Builder factory = builders.get(entry.getKey());
                    if (factory instanceof Interceptor) {
                        Object value = entry.getValue().get();
                        // we are ONLY clearing if this thread has something in of threadlocal, in other words,
                        // if of thread has previously requested this key...
                        if (value != null) listToClear.add(new ClearableHolder((Interceptor) factory, value));
                    }
                }
                // and now we remove all thread locals belonging to this thread...
                // this will only remove of instances related to this thread...
                for (ThreadLocal<Object> t : threadLocalsCache.values()) t.remove();
            }
            // remove everything inside a non-synchronized block...
            for (ClearableHolder cp : listToClear) cp.clear();
        }
    }

    public void clear() {
        builders.clear();
        scopes.clear();
        constructorDependencies.clear();
        setterDependencies.clear();
        forConstructMethod.clear();        
        clearCache();
    }
    public void clearCache() {
        singletonsCache.clear();
        threadLocalsCache.clear();        
    }
    
    @Override
    public <T> T remove(Object k) {
        final String key = InjectionUtils.getKeyName(k);
        Builder factory = builders.get(key);
        if (factory==null)
            return null;

        Scope scope = scopes.get(key);
        if (scope == Scope.SINGLETON) {
            ClearableHolder cp = null;
            Object value;
            synchronized (this) {
                value = singletonsCache.remove(key);
                if (value != null && factory instanceof Interceptor)
                    cp = new ClearableHolder((Interceptor) factory, value);
            }
            if (cp != null) cp.c.onRemoved(cp.value);
            return (T) value;
        } else if (scope == Scope.THREAD) {
            ClearableHolder cp = null;
            Object retVal = null;
            synchronized (this) {
                ThreadLocal<Object> t = threadLocalsCache.get(key);
                if (t != null) {
                    Object o = t.get();
                    if (o != null) {
                        if (factory instanceof Interceptor) {
                            cp = new ClearableHolder((Interceptor) factory, o);
                        }
                        t.remove();
                        retVal = o;
                    }
                }
            }
            if (cp != null) cp.c.onRemoved(cp.value);
            return (T) retVal;
        } else if (scope == Scope.NONE) return null; // always...
        else throw new UnsupportedOperationException("Scope not supported: " + scope);
    }


    @Override
    public synchronized boolean contains(Object obj) {
        String key = InjectionUtils.getKeyName(obj);
        if (!builders.containsKey(key)) return false;

        Scope scope = scopes.get(key);
        if (scope == Scope.NONE) return false; // always...
        else if (scope == Scope.SINGLETON) return singletonsCache.containsKey(key);
        else if (scope == Scope.THREAD) {
            ThreadLocal<Object> t = threadLocalsCache.get(key);
            return t != null && t.get() != null;
        } else throw new UnsupportedOperationException("This scope is not supported: " + scope);
    }

    public static <X> X bean(Class<? extends X> intrface) {
        //TODO see if caching the builder's (result of on()) performs best
        return BeanProxyBuilder.on(intrface).build();
    }
        
}