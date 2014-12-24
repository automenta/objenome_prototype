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
package objenome.solver.evolve;

/**
 * An instance of <code>IndividualSelector</code> represents the selection
 * strategy used during an evolutionary run to generate a new population. The
 * selection is usually guided by the fitness of the individuals.
 */
public interface IndividualSelector {

    /**
     * Prepares the selector. This method is called before an individual is
     * selected.
     *
     * @param population the current population.
     */
    public void setup(Population population);

    /**
     * Returns an individual.
     *
     * @return an individual.
     */
    public Individual select();

}
