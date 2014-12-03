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
import objenome.solution.dependency.Builder;
import objenome.solution.dependency.ClassBuilder;
import objenome.solution.dependency.ConfigurableBuilder;
import objenome.solution.dependency.ConstructorDependency;
import objenome.solution.dependency.Interceptor;
import objenome.solution.dependency.Scope;
import objenome.solution.dependency.SetterDependency;
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

    
    public Map<String, Builder> getBuilders() {
        return this.builders;
    }

        
    @Override
    public Class type(Object key) {        
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
        
        //remove existing forConstructMethod with this key
        forConstructMethod.removeIf(c -> keyString.equals(c.getSource()));
        
        forConstructMethod.add(new ConstructorDependency(keyString, b.type()));
        return b;
    }


    @Override
    public ConfigurableBuilder usable(Object key, Scope scope, Class<?> klass) {
        return (ConfigurableBuilder) usable(key, scope, new ClassBuilder(this, klass));
    }

    @Override
    public void use(Object sourceFromContainer) {
        // use by constructor and setter...
        String s = InjectionUtils.getKeyName(sourceFromContainer);
        
        SetterDependency sd = autowireBySetter(s);        
        ConstructorDependency cd = autowireByConstructor(s);
        
        cd.setContainerKey(sourceFromContainer);
        //sd.setContainerKey(sourceFromContainer);
    }

//    @Override
//    public void use(Object sourceFromContainer, String beanProperty) {
//        // use by constructor and setter...
//        String s = InjectionUtils.getKeyName(sourceFromContainer);
//        autowireBySetter(beanProperty, s);
//        autowireByConstructor(s);
//    }
 

    @Override
    public Set<ConstructorDependency> getConstructorDependencies() {
        return constructorDependencies;
    }
    
    public ClassBuilder getClassBuilder(Class c) {
        return new ClassBuilder(this, c, forConstructMethod);
    }
            

    
    private SetterDependency autowireBySetter(String targetProperty, String sourceFromContainer) {

        Class<?> sourceType = type(sourceFromContainer);        
        
        setterDependencies.removeIf(sd -> sd.getSource().equals(sourceFromContainer));
        
        SetterDependency sd;
        setterDependencies.add(
                sd = new SetterDependency(targetProperty, sourceFromContainer, sourceType)
        );
        return sd;        
    }

    private SetterDependency autowireBySetter(String targetProperty) {
        return autowireBySetter(targetProperty, targetProperty);
    }

    private ConstructorDependency autowireByConstructor(String sourceFromContainer) {

        Class<?> sourceType = type(sourceFromContainer);        
        
        constructorDependencies.removeIf(cd -> cd.getSource().equals(sourceFromContainer));
                
        ConstructorDependency c;
        constructorDependencies.add(
                c = new ConstructorDependency(sourceFromContainer, sourceType)
        );
        return c;        
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
