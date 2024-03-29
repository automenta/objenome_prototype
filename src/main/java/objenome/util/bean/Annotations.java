package objenome.util.bean;

import objenome.util.bean.anno.GenericBeanMethod;
import objenome.util.bean.anno.PropertyChangeEventMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class Annotations {

    private Annotations() {
        throw new IllegalStateException();
    }

    public static boolean isAnnotated(final Method method,
            final objenome.util.bean.anno.GenericBeanMethod.Type type) {
        final GenericBeanMethod annotation = method.getAnnotation(GenericBeanMethod.class);
        return annotation != null && annotation.value() == type;
    }

    public static boolean isAnnotated(final Method method,
            final objenome.util.bean.anno.PropertyChangeEventMethod.Type type) {
        final PropertyChangeEventMethod annotation = method.getAnnotation(PropertyChangeEventMethod.class);
        return annotation != null && annotation.value() == type;
    }

    public static boolean hasMethodWithAnnotation(final Iterable<Class<?>> ifaces,
            final Class<? extends Annotation> anno) {
        for (final Class<?> iface : ifaces) {
            if (hasMethodWithAnnotation(iface, anno)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMethodWithAnnotation(final Class<?> iface, final Class<? extends Annotation> anno) {
        for (final Method method : iface.getDeclaredMethods()) {
            if (method.isAnnotationPresent(anno)) {
                return true;
            }
        }
        return false;
    }

}