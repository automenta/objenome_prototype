package objenome.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Find constructor with polymorphism! Class.getConstructor only finds an exact
 * match.
 *
 * @author Jon Skeet
 * (http://groups.google.com/group/comp.lang.java.programmer/browse_thread/thread/921ab91865c8cc2e/9e141d3d62e7cb3f)
 */
public class FindConstructor {

    /**
     * Finds the most specific applicable constructor
     *
     * @param source Class to find a constructor for
     * @param parameterTypes Parameter types to search for
     */
    public static Constructor<?> getConstructor(Class<?> source,
            Class<?>[] parameterTypes, Map<Parameter,Object> specific)
            throws NoSuchMethodException {
        return internalFind(source.getConstructors(),
                parameterTypes, 
                specific);
    }

    /**
     * Finds the most specific applicable declared constructor
     *
     * @param source Class to find method in
     * @param parameterTypes Parameter types to search for
     */
    public static Constructor<?> getDeclaredConstructor(Class<?> source,
            Class<?>[] parameterTypes)
            throws NoSuchMethodException {
        return internalFind(source.getDeclaredConstructors(),
                parameterTypes, 
                Collections.EMPTY_MAP);
    }

    /**
     * Internal method to find the most specific applicable method
     */
    private static Constructor<?> internalFind(Constructor<?>[] toTest,
            Class<?>[] parameterTypes, Map<Parameter,Object> specific)
            throws NoSuchMethodException {

        int l = parameterTypes.length;

        // First find the applicable methods 
        List<Constructor<?>> applicableMethods = new LinkedList<Constructor<?>>();

        int assigned = 0;
        for (int i = 0; i < toTest.length; i++) {
            // Check the parameters match 
            Parameter[] params = toTest[i].getParameters();
            //Class<?>[] params = toTest[i].getParameterTypes();

            if (params.length != l) {
                continue;
            }
            int j;

            for (j = 0; j < params.length; j++) {
                Object specificValue = specific.get(params[j] );
                if (specificValue!=null) {                    
                    assigned++;
                }
                //TODO parameterTypes may be out of order or missing holes, so allow that
                else if (params[j].getType().isAssignableFrom(parameterTypes[j])) {
                    assigned++;
                }
                
            }
            
            // If so, add it to the list 
            if (assigned == params.length) {
                applicableMethods.add(toTest[i]);
            }
        }

        /* 
         * If we've got one or zero methods, we can finish 
         * the job now. 
         */
        int size = applicableMethods.size();

        if (size == 0) {
            throw new NoSuchMethodException("No such constructor!");
        }
        if (size == 1) {
            return applicableMethods.get(0);
        }

        /* 
         * Now find the most specific method. Do this in a very primitive 
         * way - check whether each method is maximally specific. If more 
         * than one method is maximally specific, we'll throw an exception. 
         * For a definition of maximally specific, see JLS section 15.11.2.2. 
         * 
         * I'm sure there are much quicker ways - and I could probably 
         * set the second loop to be from i+1 to size. I'd rather not though, 
         * until I'm sure... 
         */
        int maximallySpecific = -1; // Index of maximally specific method 

        for (int i = 0; i < size; i++) {
            int j;
            // In terms of the JLS, current is T 
            Constructor<?> current = applicableMethods.get(i);
            Parameter[] currentParams = current.getParameters();
            Class<?> currentDeclarer = current.getDeclaringClass();

            for (j = 0; j < size; j++) {
                if (i == j) {
                    continue;
                }
                // In terms of the JLS, test is U 
                Constructor<?> test = applicableMethods.get(j);
                Parameter[] testParams = test.getParameters();
                Class<?> testDeclarer = test.getDeclaringClass();

                // Check if T is a subclass of U, breaking if not 
                if (!testDeclarer.isAssignableFrom(currentDeclarer)) {
                    break;
                }

                // Check if each parameter in T is a subclass of the 
                // equivalent parameter in U 
                int k;

                for (k = 0; k < testParams.length; k++) {
                    Object specificValue = specific.get(currentParams[k] );
                    if ((specificValue==null) ||
                       (!testParams[k].getType().isAssignableFrom(currentParams[k].getType()))) {
                        break;
                    }
                }
                if (k != testParams.length) {
                    break;
                }
            }
            // Maximally specific! 
            if (j == size) {
                if (maximallySpecific != -1) {
                    throw new NoSuchMethodException("Ambiguous method search - more "
                            + "than one maximally specific method");
                }
                maximallySpecific = i;
            }
        }
        if (maximallySpecific == -1) {
            throw new NoSuchMethodException("No maximally specific method.");
        }
        return applicableMethods.get(maximallySpecific);
    }

}
