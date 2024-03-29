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

import objenome.evolve.event.GenerationEvent;
import objenome.evolve.event.Listener;

/**
 * This class represents a termination criteria based on the maximum number of
 * generations.
 */
public class MaximumGenerations implements TerminationCriteria, Listener<GenerationEvent.EndGeneration>, GP.GPContainerAware {

    /**
     * The key for setting and retrieving the maximum number of generations.
     */
    public static final GP.GPKey<Integer> MAXIMUM_GENERATIONS = new GP.GPKey<>();

    /**
     * The generation counter.
     */
    private int generation;

    /**
     * Constructs a <code>MaximumGenerations</code>.
     */
    public MaximumGenerations() {
    }
    @Override
    public void setConfig(GP config) {
        config.on(GenerationEvent.EndGeneration.class, this);
    }
    

    /**
     * Returns <code>true</code> when the maximum number of generations is
     * reached.
     *
     * @return <code>true</code> when the maximum number of generations is
     * reached; <code>false</code> otherwise.
     */
    @Override
    public boolean terminate(GP config) {
        return generation >= (Integer)config.get(MAXIMUM_GENERATIONS);
    }

    /**
     * Updates the generation counter based on the <code>EndGeneration</code>
     * event.
     *
     * @param event the <code>EndGeneration</code> event.
     */
    @Override
    public void onEvent(GenerationEvent.EndGeneration event) {
        generation = event.getGeneration();
    }


}