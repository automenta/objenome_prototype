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

import objenome.evolve.event.ConfigEvent;
import objenome.evolve.event.GenerationEvent;
import objenome.evolve.event.Listener;

import java.util.List;

/**
 * A <code>GenerationalStrategy</code> is an evolutionary strategy with clearly
 * defined generations. It consists of a main loop that is executed until a
 * termination criteria is met. At each iteration of the loop, a new population
 * is created using the pipeline's components.
 *
 * The main loop can be illustrated as:
 *
 * <pre>
 *  while (!terminate) {
 *  	generate (and optionally evaluate) new population
 *  }
 * </pre>
 *
 * The {@link TerminationCriteria} is obtained from the {@link GP}, using
 * the appropriate <code>ConfigKey</code>. A new population is generated using
 * the pipeline's components, which typically will include a {@link Breeder} and
 * {@link ScoreEvaluator} instances.
 *
 * @see Breeder
 * @see ScoreEvaluator
 * @see TerminationCriteria
 */
public class GenerationalStrategy extends Pipeline implements EvolutionaryStrategy, Listener<ConfigEvent> {

    /**
     * The list of termination criteria.
     */
    private List<TerminationCriteria> criteria;
    private GP config;

    /**
     * Constructs a <code>GenerationalStrategy</code> with the provided
     * components. One of those components would typically be a {@link Breeder}.
     *
     * @param components
     */
    public GenerationalStrategy(PopulationProcess... components) {
        for (PopulationProcess component : components) {
            add(component);
        }

    }

    /**
     * Evolves the population until the termination criteria is met. A
     * {@link GenerationEvent.StartGeneration} event is fired at the start of the generation and
     * an {@link GenerationEvent.EndGeneration} event at the end of the generation.
     *
     * @param population the population to be evolved
     *
     * @return the evolved population.
     */
    @Override
    public Population process(Population population) {
        this.config = population.getConfig();

        setup();

        config.on(ConfigEvent.class, this);

        int generation = 1;
        while (!terminate()) {
            config.fire(new GenerationEvent.StartGeneration(generation, population));

            population = super.process(population);

            config.fire(new GenerationEvent.EndGeneration(generation, population));
            generation++;
        }

        return population;
    }

    /**
     * Returns <code>true</code> if any of the termination criteria is met.
     *
     * @return <code>true</code> if any of the termination criteria is met;
     * <code>false</code> otherwise.
     */
    protected boolean terminate() {
        for (TerminationCriteria tc : criteria) {
            if (tc.terminate(config)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks up the {@link TerminationCriteria} and the {@link ScoreEvaluator}
     * in the {@link GP}.
     */
    protected void setup() {
        criteria = config.get(TERMINATION_CRITERIA);
        for (TerminationCriteria x : criteria) {
            GP.setContainerAware(config, x);
        }
    }

    /**
     * Receives {@link ConfigEvent} event notifications.
     *
     * @param event the fired event.
     */
    @Override
    public void onEvent(ConfigEvent event) {
        if (event.isKindOf(/*ProblemSTGP.PROBLEM, */TERMINATION_CRITERIA)) {
            setup();
        }
    }

}