/*
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http://www.epochx.org
 */
package objenome.op.math;

import objenome.op.Node;
import objenome.util.NumericUtils;
import objenome.util.TypeUtil;

/**
 * A node which performs the mathematical function of subtract.
 *
 * Subtraction can be performed on inputs of the following types:
 * <ul>
 * <li>Integer</li>
 * <li>Long</li>
 * <li>Float</li>
 * <li>Double</li>
 * </ul>
 *
 * Subtraction can be performed between mixed types, with a widening operation
 * performed and the result being of the wider of the two types.
 *
 * @since 2.0
 */
public class Subtract extends Node {

    public static final String IDENTIFIER = "SUB";

    /**
     * Constructs a SubtractFunction with two <code>null</code> children.
     */
    public Subtract() {
        this(null, null);
    }

    /**
     * Constructs an SubtractFunction with two numerical child nodes. When
     * evaluated, both children will be evaluated with the first subtracted from
     * the second.
     *
     * @param child1 The first child node.
     * @param child2 The second child node.
     */
    public Subtract(Node child1, Node child2) {
        super(child1, child2);
    }

    /**
     * Evaluates this function. Both child nodes are evaluated, the result of
     * both must be of numeric type. If necessary, the inputs are widened to
     * both be of the same type, then subtraction is performed and the return
     * value will be of that wider type.
     *
     * @return the result returned from the second child subtracted from the
     * value returned by the first child
     */
    @Override
    public Object evaluate() {
        Object c1 = getChild(0).evaluate();
        Object c2 = getChild(1).evaluate();

        Class<?> returnType = TypeUtil.widestNumberType(c1.getClass(), c2.getClass());

        if (returnType == Double.class) {
            // Subtract as doubles.
            double d1 = NumericUtils.asDouble(c1);
            double d2 = NumericUtils.asDouble(c2);

            return d1 - d2;
        } else if (returnType == Float.class) {
            // Subtract as floats.
            float f1 = NumericUtils.asFloat(c1);
            float f2 = NumericUtils.asFloat(c2);

            return f1 - f2;
        } else if (returnType == Long.class) {
            // Subtract as longs.
            long l1 = NumericUtils.asLong(c1);
            long l2 = NumericUtils.asLong(c2);

            return l1 - l2;
        } else if (returnType == Integer.class) {
            // Subtract as integers.
            int i1 = NumericUtils.asInteger(c1);
            int i2 = NumericUtils.asInteger(c2);

            return i1 - i2;
        }

        return null;
    }

    /**
     * Returns the identifier of this function which is SUB
     *
     * @return this node's identifier
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Returns this function node's return type for the given child input types.
     * If there are two input types of numeric type then the return type will be
     * the wider of those numeric types. In all other cases this method will
     * return <code>null</code> to indicate that the inputs are invalid.
     *
     * @return A numeric class or null if the input type is invalid.
     */
    @Override
    public Class dataType(Class... inputTypes) {
        if (inputTypes.length == 2) {
            return TypeUtil.widestNumberType(inputTypes);
        }
        return null;
    }
}
