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

import objenome.evolve.event.GenerationEvent;
import objenome.evolve.event.stat.AbstractStat;

/**
 * A stat that returns the minimum number of non-terminals in all the program
 * trees in the population from the previous completed generation. All
 * individuals in the population must be instances of
 * <code>STGPIndividual</code>.
 *
 * @see GenerationMaximumNonTerminals
 * @see GenerationMinimumTerminals
 *
 * @since 2.0
 */
public class GenerationMinimumNonTerminals extends AbstractStat<GenerationEvent.EndGeneration> {

    private int min;

    /**
     * Constructs a <code>GenerationMinimumNonTerminals</code> stat and
     * registers its dependencies
     */
    public GenerationMinimumNonTerminals() {
        super(GenerationNonTerminals.class);
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
        int[] nonTerminals = getConfig().the(GenerationNonTerminals.class).getNonTerminals();
        min = Integer.MAX_VALUE;

        for (int t : nonTerminals) {
            if (t < min) {
                min = t;
            }
        }
    }

    /**
     * Returns the minimum number of non-terminal nodes in the program trees in
     * the previous generation
     *
     * @return the minimum number of non-terminals of the program trees
     */
    public int getMinimum() {
        return min;
    }

    /**
     * Returns a string representation of the value of this stat
     *
     * @return a <code>String</code> that represents the value of this stat
     */
    @Override
    public String toString() {
        return Integer.toString(min);
    }
}