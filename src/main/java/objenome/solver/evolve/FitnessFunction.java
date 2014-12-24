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
 * The latest version is available from: http://www.epochx.org
 */
package objenome.solver.evolve;

/**
 * Implementations of <code>FitnessFunction</code> are classes that are
 * responsible for assigning fitnesses to individuals in a population. Typically
 * this will be performed by evaluating the quality of each individual against
 * problem specific requirements.
 *
 * @see Fitness
 */
public interface FitnessFunction<I extends Individual> {

    /**
     * Evaluates the individuals in the specified population.
     *
     * @param population the population to be evaluated.
     */
    public void evaluate(Population<I> population);

}
