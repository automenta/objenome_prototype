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
package objenome.gene.gp.op.math;

import objenome.gene.gp.op.Node;
import objenome.gene.gp.tools.DataTypeUtils;
import objenome.gene.gp.tools.NumericUtils;

/**
 * A node which performs the arithmetic function of cube, that is - raising to
 * the third power. It is equivalent to the <code>PowerFunction</code> where the
 * second child is the double literal 3.0.
 *
 * @since 2.0
 */
public class Cube extends Node {

    public static final String IDENTIFIER = "CUBE";

    /**
     * Constructs a CubeFunction with one <code>null</code> child.
     */
    public Cube() {
        this(null);
    }

    /**
     * Constructs a CubeFunction with one numerical child node.
     *
     * @param child the child node.
     */
    public Cube(Node child) {
        super(child);
    }

    /**
     * Evaluates this function. The child node is evaluated, the result of which
     * must be a numeric type (one of Double, Float, Long, Integer). The result
     * is raised to the power of 3 and returned as the same type as the input.
     *
     * @return the result of evaluating the child raised to the power of 3
     */
    @Override
    public Object evaluate() {
        Object c = getChild(0).evaluate();

        double result = Math.pow(NumericUtils.asDouble(c), 3);

        if (c instanceof Long) {
            return (long) result;
        } else if (c instanceof Integer) {
            return (int) result;
        } else if (c instanceof Double) {
            return result;
        } else if (c instanceof Float) {
            return (float) result;
        } else {
            return null;
        }
    }

    /**
     * Returns the identifier of this function which is CUBE
     *
     * @return this node's identifier
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Returns this function node's return type for the given child input types.
     * If there is one input type of a numeric type then the return type will be
     * that numeric type. In all other cases this method will return
     * <code>null</code> to indicate that the inputs are invalid.
     *
     * @return a numeric class or null if the input type is invalid.
     */
    @Override
    public Class<?> dataType(Class<?>... inputTypes) {
        if ((inputTypes.length == 1) && DataTypeUtils.isNumericType(inputTypes[0])) {
            return inputTypes[0];
        } else {
            return null;
        }
    }
}
