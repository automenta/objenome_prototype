package objenome;

import java.util.Set;
import objenome.impl.ConstructorDependency;

/**
 * A IoC container that provides:
 * <ul>
 * <li>Programmatic Configuration</li>
 * <li>Bean Instantiation through constructors</li>
 * <li>Bean Initialization through setters</li>
 * <li>Dependency Injection through constructors</li>
 * <li>Dependency Injection through setters</li>
 * <li>Auto-wiring through constructors and setters (very simple!)</li>
 * <li>Injection through setters so you can populate any external object with
 * objects from the container</li>
 * <li>Instantiation through constructors so you can instantiate any external
 * class with objects from the container</li>
 * <li>Support for SINGLETON and THREAD scopes, plus you can easily get REQUEST
 * and SESSION scopes for web projects</li>
 * <li>Generic Factories so you can easily turn anything into a object
 * factory</li>
 * <li>Interceptors for factories: onCreated, onCleared, useful for object
 * pooling</li>
 * </ul>
 *
 * @author sergio.oliveira.jr@gmail.com
 *
 */
public interface ProtoContext  {


    /**
     * Get the types of the instances returned by the associated factory.
     *
     * @param key The factory
     * @return The types returned by this factory
     */
    public Set<Class<?>> types(Object key);

    /**
     * Configure a bean to be returned with the given implementation when
     * {@link #get(String)} is called. An internal factory will be used.
     *
     * @param key The key representing the bean to return. The name of the bean
     * in the container.
     * @param klass The class used to instantiate the bean, in other words, its
     * implementation.
     * @param scope The scope of the factory.
     * @return The factory created as a ConfigurableBuilder. (Fluent API)
     * @see Scope
     */
    public ConfigurableBuilder usable(Object key, Class<?> klass, Scope scope);

    /**
     * Same as {@link #ioc(String, Class, Scope)} except that it assumes there
     * is no scope (Scope.NONE).
     *
     * @param key
     * @param klass
     * @return The factory created as a ConfigurableBuilder. (Fluent API)
     * @see Scope
     */
    public ConfigurableBuilder usable(Object key, Class<? extends Object> klass);

    /**
     * Set up a factory for the given key. The scope assumed is NONE.
     *
     * @param key The key representing the bean to return. The name of the bean
     * in the container.
     * @param factory The factory for the IoC.
     * @return The factory passed as a parameter. (Fluent API)
     * @see Builder
     */
    public Builder usable(Object key, Builder factory);

    /**
     * Set up a factory for the given key in the given scope.
     *
     * @param key The key representing the bean to return. The name of the bean
     * in the container.
     * @param factory The factory for the IoC.
     * @param scope The scope used by the factory.
     * @return The factory passed as a parameter (Fluent API).
     * @see Builder
     * @see Scope
     */
    public Builder usable(Object key, Builder factory, Scope scope);

    /**
     * Configure a bean dependency to be auto-wired by the container. It wires
 by constructor and by setter. By constructor is uses the types of
 sourceFromContainer. By setter it assumes the property is also named
 sourceFromContainer.
     *
     * @param sourceFromContainer The bean inside the container that will be
     * wired automatically inside any other bean the depends on it.
     */
    public void use(Object sourceFromContainer);
    
    default public ConfigurableBuilder use(Object key, Class<? extends Object> klass) {
        ConfigurableBuilder c = usable(key, klass);
        use(key);
        return c;
    }
    default public Builder use(Object key, Builder factory) {
        Builder c = usable(key, factory);
        use(key);
        return c;
    }
    
    //TODO make 'use' versions of all 'usable' methods
    
    /**
     * Configure a bean dependency to be auto-wired by the container. It wires
 by constructor and by setter. By constructor is uses the types of
 sourceFromContainer. By setter it looks for a property with the given
 name and try to apply.
     *
     * @param sourceFromContainer The bean inside the container that will be
     * wired automatically inside any other bean the depends on it.
     * @param property The name of the property to apply, whey trying
 auto-wiring by setter.
     */
    public void use(Object sourceFromContainer, String property);


    
    public Set<ConstructorDependency> getConstructorDependencies();

}
