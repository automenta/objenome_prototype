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

package objenome.gene.gp.epochx;

import java.util.ArrayList;
import java.util.Map;

import objenome.gene.gp.epochx.Config.ConfigKey;
import objenome.gene.gp.epochx.Config.Template;

/**
 * Default configuration template for generational executions.
 * 
 * @see Config
 * @see Template
 */
public class GenerationalTemplate extends Template {

	/**
	 * The default parameter values:
	 * <ul>
	 * <li>{@link Evolver#COMPONENTS}: configure the pipeline components to
	 * include {@link Initialiser}, {@link FitnessEvaluator} and
	 * {@link GenerationalStrategy}.
	 */
	@Override
	protected void fill(Map<ConfigKey<?>, Object> template) {
		ArrayList<Component> components = new ArrayList<Component>();
		components.add(new Initialiser());
		components.add(new FitnessEvaluator());
		components.add(new GenerationalStrategy(new BranchedBreeder(), new FitnessEvaluator()));
		template.put(Evolver.COMPONENTS, components);
	}

}