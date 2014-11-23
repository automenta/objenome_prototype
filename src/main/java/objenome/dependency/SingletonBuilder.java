package objenome.dependency;

import objenome.AbstractContainer;

public class SingletonBuilder implements Builder {

    public final Object instance;

    public final Class<?> type;

    public SingletonBuilder(final Object instance) {

        this.instance = instance;

        this.type = instance.getClass();
    }

    @Override
    public <T> T instance(AbstractContainer context) {
 
        return (T) instance;
    }

    @Override
    public Class<?> type() {

        return type;
    }
}
