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
 * A node which performs the common (base 10) logarithm, called LOG-10
 *
 * @see Log
 *
 * @since 2.0
 */
public class Log10 extends Node {

    public static final String IDENTIFIER = "LOG10";

    /**
     * Constructs a Log10Function with one <code>null</code> child.
     */
    public Log10() {
        this(null);
    }

    /**
     * Constructs a Log10Function with one numerical child nodes.
     *
     * @param child The child of which the base 10 logarithm will be calculated.
     */
    public Log10(Node child) {
        super(child);
    }

    /**
     * Evaluates this function. The child node is evaluated, the result of which
     * must be of a numeric type (one of Byte, Short, Integer, Long). The base
     * 10 logarithm is performed on this value and the result returned as a
     * Double.
     *
     * @return the base 10 logarithm performed on the value returned from the
     * child
     */
    @Override
    public Double evaluate() {
        double c = NumericUtils.asDouble(getChild(0).evaluate());

        return Math.log10(c);
    }

    /**
     * Returns the identifier of this function which is LOG-10
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
     * Double. In all other cases this method will return <code>null</code> to
     * indicate that the inputs are invalid.
     *
     * @return the Double class or null if the input type is invalid.
     */
    @Override
    public Class<?> dataType(Class<?>... inputTypes) {
        if ((inputTypes.length == 1) && DataTypeUtils.isAllNumericType(inputTypes)) {
            return Double.class;
        }

        return null;
    }
}
