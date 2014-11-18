/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import objenome.impl.ClassBuilder;
import objenome.impl.ConstructorDependency;
import objenome.impl.SetterDependency;
import objenome.util.InjectionUtils;

/**
 *
 * @author me
 */
public class AbstractProtoContext implements ProtoContext  {
    
    protected Map<String,Builder> builders = new HashMap();

    protected Map<String, Scope> scopes = new HashMap<String, Scope>();
    
    protected Set<SetterDependency> setterDependencies = Collections.synchronizedSet(new HashSet<SetterDependency>());
    protected Set<ConstructorDependency> constructorDependencies = Collections.synchronizedSet(new HashSet<ConstructorDependency>());
    protected Set<ConstructorDependency> forConstructMethod = Collections.synchronizedSet(new HashSet<ConstructorDependency>());

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
