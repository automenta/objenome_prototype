/**
 * Copyright (C) 2009 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package objenome.util.bytecode.factory;

import objenome.util.bytecode.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates an implementation of an interface.
 */
public class ImplementationFactory {

    private final boolean onlyDeclaredMethods;

    private final SgClassPool pool;

    /**
     * Constructor with class pool. All methods will be implemented (declared
     * and super interface methods). If you just want to implement declared
     * methods use the {@link #ImplementationFactory(SgClassPool, boolean)}
     * constructor.
     * 
     * @param pool
     *            Class pool.
     */
    public ImplementationFactory(final SgClassPool pool) {
        this(pool, false);
    }

    /**
     * Constructor with class pool and information about methods to implement.
     * 
     * @param pool
     *            Class pool.
     * @param onlyDeclaredMethods
     *            Should only declared methods be implemented?
     */
    public ImplementationFactory(final SgClassPool pool, final boolean onlyDeclaredMethods) {
        super();

        assureNotNull("pool", pool);
        this.pool = pool;

        this.onlyDeclaredMethods = onlyDeclaredMethods;

    }

    /**
     * Creates an implementation of the interface.
     * 
     * @param implPackageName
     *            Name of the implementation package - Cannot be null.
     * @param implClassName
     *            Name of the implementation class - Cannot be null.
     * @param listener
     *            Creates the bodies for all methods - Cannot be null.
     * @param intf
     *            One or more interfaces.
     * 
     * @return New object implementing the interface.
     */
    public final SgClass create(final String implPackageName, final String implClassName,
            final ImplementationFactoryListener listener, final Class<?>... intf) {
        return create(implPackageName, implClassName, null, null, listener, intf);
    }

    /**
     * Creates an implementation of the interface.
     * 
     * @param implPackageName
     *            Name of the implementation package - Cannot be null.
     * @param implClassName
     *            Name of the implementation class - Cannot be null.
     * @param superClass
     *            Parent class or <code>null</code>.
     * @param enclosingClass
     *            Outer class or <code>null</code>.
     * @param listener
     *            Creates the bodies for all methods - Cannot be null.
     * @param intf
     *            One or more interfaces.
     * 
     * @return New object implementing the interface.
     */
    public final SgClass create(final String implPackageName, final String implClassName,
            final SgClass superClass, final SgClass enclosingClass,
            final ImplementationFactoryListener listener, final Class<?>... intf) {

        assureNotNull("implPackageName", implPackageName);
        assureNotNull("implClassName", implClassName);
        assureNotNull("listener", listener);
        assureNotNull("intf", intf);
        assureNotEmpty("intf", intf);
        assureAllInterfaces(intf);

        // Create class with all interfaces
        final SgClass clasz = new SgClass("public", implPackageName, implClassName, superClass,
                false, enclosingClass);
        for (Class<?> value : intf) {
            clasz.addInterface(SgClass.create(pool, value));
        }
        listener.afterClassCreated(clasz);

        final Map<String, ImplementedMethod> implMethods = new HashMap<>();

        // Iterate through interfaces and add methods
        for (Class<?> aClass : intf) {
            addInterfaceMethods(implMethods, clasz, aClass, listener);
        }

        // Iterate through methods and create body
        for (ImplementedMethod implMethod : implMethods.values()) {
            final SgMethod method = implMethod.getMethod();
            final Class<?>[] interfaces = implMethod.getInterfaces();
            final List<String> lines = listener.createBody(method, interfaces);
            for (String line : lines) {
                implMethod.getMethod().addBodyLine(line);
            }
        }

        return clasz;
    }

    private void addInterfaceMethods(final Map<String, ImplementedMethod> implMethods,
            final SgClass clasz, final Class<?> intf, final ImplementationFactoryListener listener) {

        final Method[] methods;
        if (onlyDeclaredMethods) {
            methods = intf.getDeclaredMethods();
        } else {
            methods = intf.getMethods();
        }
        for (Method value : methods) {

            // Create method signature
            final String name = value.getName();
            final String typeSignature = SgUtils.createTypeSignature(name, value
                    .getParameterTypes());

            // Get return type
            final SgClass returnType;
            if (value.getReturnType() == null) {
                returnType = SgClass.VOID;
            } else {
                returnType = SgClass.create(pool, value.getReturnType());
            }

            // Check if we already implemented this method
            ImplementedMethod implMethod = implMethods.get(typeSignature);
            if (implMethod == null) {
                final SgMethod method = new SgMethod(clasz, "public", returnType, name);
                // Add arguments
                final Class<?>[] paramTypes = value.getParameterTypes();
                for (int k = 0; k < paramTypes.length; k++) {
                    final SgClass paramType = SgClass.create(pool, paramTypes[k]);
                    method.addArgument(new SgArgument(method, paramType, ("arg" + k)));
                }
                method.addAnnotations(SgUtils.createAnnotations(value.getAnnotations()));
                implMethod = new ImplementedMethod(method);
                implMethod.addInterface(intf);
                implMethods.put(typeSignature, implMethod);
            } else {
                implMethod.addInterface(intf);
                if (!returnType.getName().equals(implMethod.getReturnType().getName())) {
                    final StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < implMethod.getInterfaces().length; i++) {
                        if (i > 0) {
                            sb.append("' or '");
                        }
                        sb.append(implMethod.getInterfaces()[i].getName());
                    }
                    throw new IllegalArgumentException("Method '" + typeSignature
                            + "' has different return types for interface '" + intf.getName()
                            + "' and '" + sb + "'!");
                }
            }

            // Add exceptions if missing
            final SgMethod method = implMethod.getMethod();
            final Class<?>[] exceptionTypes = value.getExceptionTypes();
            for (Class<?> exceptionType : exceptionTypes) {
                final SgClass ex = SgClass.create(pool, exceptionType);
                if (!method.getExceptions().contains(ex)) {
                    method.addException(ex);
                }
            }

        }

    }

    private static void assureNotNull(final String name, final Object value) {
        if (value == null) {
            throw new IllegalArgumentException("The argument '" + name + "' cannot be null!");
        }
    }

    private static void assureNotEmpty(final String name, final Object[] value) {
        if (value.length == 0) {
            throw new IllegalArgumentException("The argument '" + name
                    + "' cannot be an empty array!");
        }
    }

    private static void assureAllInterfaces(final Class<?>... intf) {
        for (int i = 0; i < intf.length; i++) {
            if (!intf[i].isInterface()) {
                throw new IllegalArgumentException("Expected an interface: " + intf[i].getName()
                        + " [" + i + "]");
            }
        }
    }

}