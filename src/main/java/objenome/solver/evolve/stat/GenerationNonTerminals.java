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
package objenome.solver.evolve.stat;

import java.util.Arrays;
import objenome.solver.evolve.Individual;
import objenome.solver.evolve.Population;
import objenome.solver.evolve.STGPIndividual;
import objenome.solver.evolve.event.GenerationEvent.EndGeneration;
import objenome.solver.evolve.event.stat.AbstractStat;

/**
 * A stat that returns the number of non-terminals in all program trees in the
 * population from the previous generation. All individuals in the population
 * must be instances of <code>STGPIndividual</code>.
 *
 * @since 2.0
 */
public class GenerationNonTerminals extends AbstractStat<EndGeneration> {

    private int[] nonTerminals;

    /**
     * Constructs a <code>GenerationNonTerminals</code> stat and registers its
     * dependencies
     */
    public GenerationNonTerminals() {
        super(NO_DEPENDENCIES);
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
        Population<?> population = event.getPopulation();
        nonTerminals = new int[population.size()];
        int index = 0;

        for (Individual individual : population) {
            if (individual instanceof STGPIndividual) {
                nonTerminals[index++] = ((STGPIndividual) individual).getRoot().countNonTerminals();
            }
        }
    }

    /**
     * Returns an array containing the number of non-terminal nodes in each
     * program tree in the population
     *
     * @return the number of non-terminal nodes in each program tree in the
     * previous generation
     */
    public int[] getNonTerminals() {
        return nonTerminals;
    }

    /**
     * Returns a string representation of the value of this stat
     *
     * @return a <code>String</code> that represents the value of this stat
     */
    @Override
    public String toString() {
        return Arrays.toString(nonTerminals);
    }
}
