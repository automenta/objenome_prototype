package objenome.solution.dependency;

import objenome.AbstractContainer;

/**
 * An IoC factory that knows how to create instances.
 *
 * @author sergio.oliveira.jr@gmail.com
 */
public interface Builder {

    public static Class[] the(final Class<?> c) {
        return new Class[] { c };
    }
    
    public static Class[] of(final Class<?>... c) {
        return c;
    }
    
    /**
     * Returns an instance. Creates one if necessary.
     *
     * @return an instance
     */
    public <T> T instance(AbstractContainer context);

    /**
     * Return of type of objects that this factory disposes.
     *
     * @return of type of objects returned by this factory.
     */
    public Class type();
}
