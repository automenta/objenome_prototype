package objenome;

/**
 * An IoC factory that knows how to create instances.
 *
 * @author sergio.oliveira.jr@gmail.com
 */
public interface Builder {

    /**
     * Returns an instance. Creates one if necessary.
     *
     * @return an instance
     */
    public <T> T instance(Context context);

    /**
     * Return the type of objects that this factory disposes.
     *
     * @return the type of objects returned by this factory.
     */
    public Class<?> type();
}
