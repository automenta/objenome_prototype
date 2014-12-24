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
package objenome.solver.evolve.event.stat;

import java.util.HashSet;
import objenome.solver.evolve.Fitness;
import objenome.solver.evolve.event.GenerationEvent.EndGeneration;

/**
 * Stats representing the number of different fitness values in a generation.
 */
public class GenerationFitnessDiversity extends AbstractStat<EndGeneration> {

    /**
     * The number of different fitness values.
     */
    private int diversity = 0;

    /**
     * Constructs a <code>GenerationFitnessDiversity</code>.
     */
    public GenerationFitnessDiversity() {
        super(GenerationFitnesses.class);
    }

    /**
     * Computes the number of different fitness values.
     *
     * @param event the <code>EndGeneration</code> event object.
     */
    @Override
    public void refresh(EndGeneration event) {
        Fitness[] fitnesses = getConfig().the(GenerationFitnesses.class).getFitnesses();
        HashSet<String> unique = new HashSet<>();

        for (Fitness fitness : fitnesses) {
            unique.add(fitness.toString());
        }

        diversity = unique.size();
    }

    /**
     * Returns a string representation of the number of different fitness vaues.
     *
     * @return a string representation of the number of different fitness vaues.
     */
    @Override
    public String toString() {
        return Integer.toString(diversity);
    }

}
