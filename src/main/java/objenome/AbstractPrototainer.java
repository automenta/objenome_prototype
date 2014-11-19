/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open of template in of editor.
 */
package objenome;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import objenome.impl.ClassBuilder;
import objenome.impl.ConstructorDependency;
import objenome.impl.SetterDependency;
import objenome.util.InjectionUtils;

/**
 *
 * @author me
 */
public class AbstractPrototainer implements Prototainer  {
    
    protected final Map<String,Builder> builders;

    protected final Map<String, Scope> scopes;
    
    protected final Set<SetterDependency> setterDependencies;
    protected final Set<ConstructorDependency> constructorDependencies;
    protected final Set<ConstructorDependency> forConstructMethod;
    public final boolean concurrent;

    
    public AbstractPrototainer(final boolean concurrent) {
        this(
            concurrent ? new ConcurrentHashMap() : new HashMap(),
            concurrent ? new ConcurrentHashMap() : new HashMap(),
            concurrent ? Collections.synchronizedSet(new HashSet()) : new HashSet(),
            concurrent ? Collections.synchronizedSet(new HashSet()) : new HashSet(),
            concurrent ? Collections.synchronizedSet(new HashSet()) : new HashSet()
        );
    }

            
    public AbstractPrototainer(
            Map<String,Builder> builders, 
            Map<String, Scope> scopes,
            Set<SetterDependency> setterDependencies, 
            Set<ConstructorDependency> constructorDependencies,
            Set<ConstructorDependency> forConstructMethod
            ) {
        this.builders = builders;
        this.scopes = scopes;
        this.setterDependencies = setterDependencies;
        this.constructorDependencies = constructorDependencies;
        this.forConstructMethod = forConstructMethod;
        this.concurrent = builders instanceof ConcurrentHashMap;
    }

    
    @Override
    public Class<?> type(Object key) {        
        Builder f = getBuilder(key);
        if (f == null) {
            return null;
        }
        return f.type();
    }

 
    public Builder getBuilder(Object key) {
        String k = InjectionUtils.getKeyName(key);
        return builders.get(k);        
    }

    
    @Override
    public Builder usable(Object key, Scope scope, Builder b) {
        String keyString = InjectionUtils.getKeyName(key);
        builders.put(keyString, b);
        scopes.put(keyString, scope);
        forConstructMethod.add(new ConstructorDependency(keyString, b.type()));
        return b;
    }

    @Override
    public Builder usable(Object key, Builder factory) {
        return usable(key, Scope.NONE, factory);
    }

    @Override
    public ConfigurableBuilder usable(Object key, Class<?> klass) {
        return usable(key, Scope.NONE, klass);
    }

    @Override
    public ConfigurableBuilder usable(Object key, Scope scope, Class<?> klass) {
        return (ConfigurableBuilder) usable(key, scope, new ClassBuilder(this, klass));
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
 

    @Override
    public Set<ConstructorDependency> getConstructorDependencies() {
        return constructorDependencies;
    }
    
    public ClassBuilder getClassBuilder(Class c) {
        return new ClassBuilder(this, c, forConstructMethod);
    }
            

    
    private void autowireBySetter(String targetProperty, String sourceFromContainer) {

        Class<?> sourceType = type(sourceFromContainer);        
        
        setterDependencies.add(
                new SetterDependency(targetProperty, sourceFromContainer, sourceType)
        );
        
    }

    private void autowireBySetter(String targetProperty) {

        autowireBySetter(targetProperty, targetProperty);
    }

    private void autowireByConstructor(String sourceFromContainer) {

        Class<?> sourceType = type(sourceFromContainer);        
        
        constructorDependencies.add(
                new ConstructorDependency(sourceFromContainer, sourceType)
        );
        
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
