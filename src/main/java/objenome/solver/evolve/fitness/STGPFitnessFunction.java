/* 
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX
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
 * The latest version is available from: http:/www.epochx.org
 */
package objenome.solver.evolve.fitness;

import objenome.solver.evolve.AbstractFitnessFunction;
import objenome.solver.evolve.GPContainer.GPContainerAware;
import objenome.solver.evolve.GPContainer.GPKey;
import objenome.op.Variable;

/**
 * A fitness function for evaluating STGP individuals.
 *
 * @since 2.0
 */
public abstract class STGPFitnessFunction extends AbstractFitnessFunction implements GPContainerAware {

    /**
     * The key for setting the program's input variables
     */
    public static final GPKey<Variable[]> INPUT_VARIABLES = new GPKey<>();

    /**
     * The key for setting the sets of values to use as inputs
     */
    public static final GPKey<Object[][]> INPUT_VALUE_SETS = new GPKey<>();

}
