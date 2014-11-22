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
package objenome.gene.gp.stat;

import objenome.gene.gp.event.GenerationEvent.EndGeneration;
import objenome.gene.gp.event.stat.AbstractStat;

/**
 * A stat that returns the average number of terminals in the program trees in
 * the population from the previous generation. All individuals in the
 * population must be instances of <code>STGPIndividual</code>.
 *
 * @see GenerationAverageTerminalsError
 * @see GenerationStandardDeviationTerminals
 *
 * @since 2.0
 */
public class GenerationAverageTerminals extends AbstractStat<EndGeneration> {

    private double average;

    /**
     * Constructs a <code>GenerationAverageTerminals</code> stat and registers
     * its dependencies
     */
    public GenerationAverageTerminals() {
        super(GenerationTerminals.class);
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
    public void refresh(EndGeneration event) {
        int[] terminals = AbstractStat.get(GenerationTerminals.class).getTerminals();
        average = 0;

        for (int t : terminals) {
            average += t;
        }

        average /= terminals.length;
    }

    /**
     * Returns the mean number of terminal nodes found in the program trees in
     * the population from the previous generation
     *
     * @return the average number of terminals in the previous generation
     */
    public double getAverage() {
        return average;
    }

    /**
     * Returns a string representation of the value of this stat
     *
     * @return a <code>String</code> that represents the value of this stat
     */
    @Override
    public String toString() {
        return Double.toString(average);
    }
}
