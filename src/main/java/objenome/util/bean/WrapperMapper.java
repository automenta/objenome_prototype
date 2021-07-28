package objenome.util.bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps primitive types to their default values.
 * 
 * @author Joachim Baumann
 * @author Peter Fichtner
 */
public final class WrapperMapper {

    private static final Map<String, Object> MAPPING = Collections.unmodifiableMap(createMapping());

    /** All methods are static */
    private WrapperMapper() {
        throw new IllegalStateException();
    }

    private static Map<String, Object> createMapping() {
        final Map<String, Object> map = new HashMap<>();
        map.put(Double.TYPE.getName(), (double) 0);
        map.put(Float.TYPE.getName(), (float) 0);
        map.put(Long.TYPE.getName(), 0L);
        map.put(Integer.TYPE.getName(), 0);
        map.put(Short.TYPE.getName(), (short) 0);
        map.put(Character.TYPE.getName(), (char) 0);
        map.put(Byte.TYPE.getName(), (byte) 0);
        map.put(Boolean.TYPE.getName(), Boolean.FALSE);
        return map;
    }

    public static Object getNullObject(final Class<?> primitiveType) {
        return getNullObject(primitiveType.getName());
    }

    public static Object getNullObject(final String primitiveTypeName) {
        return MAPPING.get(primitiveTypeName);
    }
}