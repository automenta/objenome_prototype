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
package objenome.evolve.event.stat;

import objenome.evolve.Score;
import objenome.evolve.event.GenerationEvent;

/**
 * Stat that provides the information of the best fitness of a generation.
 */
public class GenerationBestFitness extends AbstractStat<GenerationEvent.EndGeneration> {

    /**
     * The best fitness value of a generation.
     */
    private Score best;

    /**
     * Constructs a <code>GenerationBestFitness</code>.
     */
    public GenerationBestFitness() {
        super(GenerationFitnesses.class);
    }

    /**
     * Determines the best fitness value of a generation.
     *
     * @param event the <code>EndGeneration</code> event object.
     */
    @Override
    public void refresh(GenerationEvent.EndGeneration event) {
        AbstractStat<GenerationEvent.EndGeneration> x = getConfig().the(GenerationFitnesses.class);
        Score[] fitnesses = getConfig().the(GenerationFitnesses.class).getFitnesses();
        best = null;

        for (Score fitness : fitnesses) {
            if (best == null || fitness.compareTo(best) > 0) {
                best = fitness;
            }
        }
    }

    /**
     * Returns the best fitness value.
     *
     * @return the best fitness value.
     */
    public Score getBest() {
        return best;
    }

    /**
     * Returns a string representation of the best fitness value.
     *
     * @return a string representation of the best fitness value.
     */
    @Override
    public String toString() {
        return best.toString();
    }

}