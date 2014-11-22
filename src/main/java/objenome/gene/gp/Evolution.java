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
package objenome.gene.gp;

import java.util.ArrayList;
import static objenome.gene.gp.STProblem.PROBLEM;

/**
 * Evolutionary simulation
 * <ol>
 * <li>{@link Initialiser}
 * <li>{@link FitnessEvaluator}
 * <li>{@link EvolutionaryStrategy}
 * </ol>
 *
 * The specific list of components used is obtained from the {@link GPContainer},
 * using the appropriate <code>Class</code> {@link #COMPONENTS}.
 */
public class Evolution<I extends Individual> extends GPContainer {

    /**
     * The key for setting and retrieving the list of components.
     */
    public static final ConfigKey<ArrayList<Component>> COMPONENTS = new ConfigKey<ArrayList<Component>>();

    /**
     * Constructs an <code>Evolver</code>.
     */
    public Evolution() {
        super();
    }

    /**
     * Performs an evolutionary run. Each component in the pipeline returned by
     * the <code>setupPipeline</code> method is processed in sequence. An empty
     * {@link Population} is provided to the first component, and each
     * succeeding component is supplied with the <code>Population</code>
     * returned by the previous component.
     *
     * @return a <code>Population</code> that is the result of processing the
     * pipeline of components, as returned by the final component in that
     * pipeline
     */
    public Population<I> run() {
        Pipeline pipeline = new Pipeline();

        /* Initialises the supplied <code>Pipeline</code> with the components that
         * an evolutionary run is composed of. The specific list of components used
         * is obtained from the {@link Config}, using the appropriate <code>Class</code> */
        for (Component component : get(COMPONENTS)) {
            GPContainer.makeConfigAware(this, component);
            pipeline.add(component);
        }

        //config.fire(new StartRun(0));
        Population<I> population = pipeline.process(new Population<I>(this));

        //config.fire(new EndRun(0, population));
        return population;
    }

    
    public Evolution<I> solve(STProblem problem) {
        set(PROBLEM, problem);
        return this;
    }
}
