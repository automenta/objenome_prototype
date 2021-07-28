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
 * A node which performs the mathematical function of addition.
 *
 * Addition can be performed on inputs of the following types:
 * <ul>
 * <li>Integer</li>
 * <li>Long</li>
 * <li>Float</li>
 * <li>Double</li>
 * </ul>
 *
 * Addition can be performed between mixed types, with a widening operation
 * performed and the result being of the wider of the two types.
 *
 * @since 2.0
 */
public class Add extends Node {

    public static final String IDENTIFIER = "ADD";

    /**
     * Constructs an AddFunction with two <code>null</code> children.
     */
    public Add() {
        this(null, null);
    }

    /**
     * Constructs an AddFunction with two numerical child nodes. When evaluated,
     * both children will be evaluated and added together.
     *
     * @param child1 The first child node.
     * @param child2 The second child node.
     */
    public Add(Node child1, Node child2) {
        super(child1, child2);
    }

    /**
     * Evaluates this function. Both child nodes are evaluated, the result of
     * both must be of numeric type. If necessary, the inputs are widened to
     * both be of the same type, then addition is performed and the return value
     * will be of that wider type.
     *
     * @return the sum of the inputs after evaluating the two children
     */
    @Override
    public Object evaluate() {
        Object c1 = node(0).evaluate();
        Object c2 = node(1).evaluate();

        Class<?> returnType = TypeUtil.widestNumberType(c1.getClass(), c2.getClass());

        if (returnType == Double.class) {
            return NumericUtils.asDouble(c1) + NumericUtils.asDouble(c2);
        } else if (returnType == Float.class) {
            return NumericUtils.asFloat(c1) + (float) NumericUtils.asFloat(c2);
        } else if (returnType == Long.class) {
            return NumericUtils.asLong(c1) + (long) NumericUtils.asLong(c2);
        } else if (returnType == Integer.class) {
            return (int) NumericUtils.asInteger(c1) + NumericUtils.asInteger(c2);
        }

        return null;
    }

    /**
     * Returns the identifier of this function which is ADD
     *
     * @return this node's identifier
     */
    @Override
    public String id() {
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
        return inputTypes.length == 2 ? TypeUtil.widestNumberType(inputTypes) : null;
    }
}