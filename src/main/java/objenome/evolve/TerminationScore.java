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
package objenome.evolve;

import objenome.evolve.GP.GPContainerAware;
import objenome.evolve.event.GenerationEvent;
import objenome.evolve.event.Listener;

/**
 * This class represents a termination criteria based on a desired fitness
 * value. Given a threshold fitness value, the run is terminated when the
 * current fitness is equal to or greater than the threshold fitness.
 */
public class TerminationScore implements TerminationCriteria, Listener<GenerationEvent.EndGeneration>, GPContainerAware {

    /**
     * The threshold fitness value.
     */
    private final Score threshold;

    /**
     * The current best fitness value.
     */
    private Score fittest;

    /**
     * Constructs a <code>TerminationFitness</code>.
     *
     * @param threshold the threshold fitness value.
     */
    public TerminationScore(Score threshold) {
        this.threshold = threshold;
    
    }

    @Override
    public void setConfig(GP c) {
        c.on(GenerationEvent.EndGeneration.class, this);
    }

    /**
     * Returns <code>true</code> the current fitness is equal to or greater than
     * the threshold fitness.
     *
     * @return <code>true</code> if the current fitness is equal to or greater
     * than the threshold fitness; <code>false</code> otherwise.
     */
    @Override
    public boolean terminate(GP config) {
        return fittest != null && fittest.compareTo(threshold) >= 0;
    }

    /**
     * Retireves the current best fitness from the population.
     *
     * @param event the event to get the popuation from.
     */
    @Override
    public void onEvent(GenerationEvent.EndGeneration event) {
        fittest = event.getPopulation().best().score();
    }


}