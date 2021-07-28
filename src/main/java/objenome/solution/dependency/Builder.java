package objenome.solution.dependency;

import objenome.AbstractContainer;
import objenome.solution.dependency.ClassBuilder.DependencyKey;

import java.util.Collection;

/**
 * An IoC factory that knows how to create instances.
 *
 * @author sergio.oliveira.jr@gmail.com
 */
public interface Builder {

    static Class[] the(final Class<?> c) {
        return new Class[] { c };
    }
    
    static Class[] of(final Class<?>... c) {
        return c;
    }
    
    /**
     * Returns an instance. Creates one if necessary.
     *
     * @return an instance, unless simulateAndAddExtraProperty is not null in which
     * case null should be returned and any discovered problems added to the 
     * supplied collection
     * 
     */
    <T> T instance(objenome.Prototainer context, Collection<DependencyKey> simulateAndAddExtraProblemsHere);

    default <T> T instance(AbstractContainer context) {
        return instance(context, null);
    }
    
    
    /**
     * Return of type of objects that this factory disposes.
     *
     * @return of type of objects returned by this factory.
     */
    Class type();
}