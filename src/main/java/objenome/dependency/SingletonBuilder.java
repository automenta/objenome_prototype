package objenome.dependency;

import objenome.Prototainer;
import objenome.dependency.ClassBuilder.DependencyKey;

import java.util.Collection;

public class SingletonBuilder implements Builder {

    public final Object instance;

    public final Class<?> type;

    public SingletonBuilder(final Object instance) {

        this.instance = instance;

        this.type = instance.getClass();
    }

    @Override
    public <T> T instance(Prototainer context, Collection<DependencyKey> simulateAndAddExtraProblemsHere) {
 
        if (simulateAndAddExtraProblemsHere==null) {
            return (T) instance;
        }
        
        return null;
    }

    @Override
    public Class<?> type() {

        return type;
    }
}