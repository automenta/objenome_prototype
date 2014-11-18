/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import objenome.impl.ClassFactory;
import objenome.impl.ConstructorDependency;
import objenome.impl.SetterDependency;
import objenome.util.InjectionUtils;

/**
 *
 * @author me
 */
public class AbstractProtoContext implements ProtoContext  {
    
    protected SetMultimap<String,Builder> builders = LinkedHashMultimap.create();

    protected Map<String, Scope> scopes = new HashMap<String, Scope>();
    
    protected Set<SetterDependency> setterDependencies = Collections.synchronizedSet(new HashSet<SetterDependency>());
    protected Set<ConstructorDependency> constructorDependencies = Collections.synchronizedSet(new HashSet<ConstructorDependency>());
    protected Set<ConstructorDependency> forConstructMethod = Collections.synchronizedSet(new HashSet<ConstructorDependency>());

    @Override
    public Set<Class<?>> types(Object key) {
        String k = InjectionUtils.getKeyName(key);
        Set<Builder> f = getBuilders(k);
        if (f == null) {
            return null;
        }
        return f.stream().map(b -> b.type()).collect(toSet());
    }

    protected Set<Builder> getBuilders(String name) {
        return builders.get(name);
    }
    
    protected Builder getTheBuilder(String name) {
        Set<Builder> f = getBuilders(name);
        if (f == null)
            return null;
        if (f.size() > 1) {
            throw new RuntimeException("Ambiguousity: " + name + " has builders: " + f);
        }
        return f.iterator().next();
    }
            

    
    @Override
    public Builder usable(Object key, Builder factory, Scope scope) {
        String keyString = InjectionUtils.getKeyName(key);
        builders.put(keyString, factory);
        scopes.put(keyString, scope);
        forConstructMethod.add(new ConstructorDependency(keyString, factory.type()));
        return factory;
    }

    @Override
    public Builder usable(Object key, Builder factory) {
        return usable(key, factory, Scope.NONE);
    }

    @Override
    public ConfigurableBuilder usable(Object key, Class<?> klass) {
        ConfigurableBuilder cc = new ClassFactory(this, klass);
        usable(key, cc);
        return cc;
    }

    @Override
    public ConfigurableBuilder usable(Object key, Class<?> klass, Scope scope) {
        ConfigurableBuilder cc = new ClassFactory(this, klass);
        usable(key, cc, scope);
        return cc;
    }

    @Override
    public void use(Object sourceFromContainer) {
        // use by constructor and setter...
        String s = InjectionUtils.getKeyName(sourceFromContainer);
        autowireBySetter(s);
        autowireByConstructor(s);
    }

    @Override
    public void use(Object sourceFromContainer, String beanProperty) {
        // use by constructor and setter...
        String s = InjectionUtils.getKeyName(sourceFromContainer);
        autowireBySetter(beanProperty, s);
        autowireByConstructor(s);
    }
 

    public Set<ConstructorDependency> getConstructorDependencies() {
        return constructorDependencies;
    }

    
    private void autowireBySetter(String targetProperty, String sourceFromContainer) {

        Set<Class<?>> sourceTypes = types(sourceFromContainer);        
        for (Class t : sourceTypes) {            
            setterDependencies.add(
                    new SetterDependency(targetProperty, sourceFromContainer, t)
            );
        }
    }

    private void autowireBySetter(String targetProperty) {

        autowireBySetter(targetProperty, targetProperty);
    }

    private void autowireByConstructor(String sourceFromContainer) {

        Set<Class<?>> sourceTypes = types(sourceFromContainer);        
        for (Class t : sourceTypes) {     
            constructorDependencies.add(
                    new ConstructorDependency(sourceFromContainer, t)
            );
        }
    }

    protected static class ClearableHolder {

        public final Interceptor c;
        public final Object value;

        public ClearableHolder(Interceptor c, Object value) {
            this.c = c;
            this.value = value;
        }

        public void clear() {
            c.onRemoved(value);
        }

    }
    
}
