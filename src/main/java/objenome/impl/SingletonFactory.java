package objenome.impl;

import objenome.Builder;
import objenome.AbstractContainer;

public class SingletonFactory implements Builder {

    public final Object instance;

    public final Class<?> type;

    public SingletonFactory(final Object instance) {

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
