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
package objenome.evolve.stat;

import objenome.evolve.Population;
import objenome.evolve.event.GenerationEvent;
import objenome.evolve.event.stat.AbstractStat;

/**
 * A stat that returns the standard error of the mean depth, as calculated by
 * the {@link GenerationAverageDepth} stat. All individuals in the population
 * must be instances of <code>STGPIndividual</code>.
 *
 * @see GenerationAverageDepth
 * @see GenerationStandardDeviationDepth
 *
 * @since 2.0
 */
public class GenerationAverageDepthError extends AbstractStat<GenerationEvent.EndGeneration> {

    private double error;

    /**
     * Constructs a <code>GenerationAverageDepthError</code> stat and registers
     * its dependencies
     */
    public GenerationAverageDepthError() {
        super(GenerationStandardDeviationDepth.class);
    }

    /**
     * Triggers the generation of an updated value for this stat. Once this stat
     * has been registered, this method will be called on each
     * <code>EndGeneration</code> event.
     *
     * @param event an object that encapsulates information about the event that
     * occurred
     */
    @Override
    public void refresh(GenerationEvent.EndGeneration event) {
        double stdev = getConfig().the(GenerationStandardDeviationDepth.class).getStandardDeviation();
        Population<?> population = event.getPopulation();

        error = stdev / Math.sqrt(population.size());
    }

    /**
     * Returns the standard error of the mean depth of the program trees in the
     * previous generation
     *
     * @return the error of the mean depth of the program trees
     */
    public double getError() {
        return error;
    }

    /**
     * Returns a string representation of the value of this stat
     *
     * @return a <code>String</code> that represents the value of this stat
     */
    @Override
    public String toString() {
        return Double.toString(error);
    }
}