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
 * The latest version is available from: http:/www.epochx.org
 */
package objenome.solver.evolve;

public abstract class AbstractScorer<I extends Individual> implements ScoreFunction<I> {

    /**
     * Evaluates all individuals in the given population by calling the
     * <code>evaluate(Individual)</code> method and if the individual is an
     * instance of <code>AbstractIndividual</code> the fitness is assigned to
     * it.
     *
     * @param
     */
    @Override
    public void evaluate(Population<I> population) {
        for (I individual : population) {
            Score fitness = evaluate(population, individual);
            assignScore(fitness, individual);
        }
    }

    /**
     * Implementations should
     *
     * @param individual
     * @return the fitness of the given individual
     */
    public abstract Score evaluate(Population population, Individual individual);


    protected void assignScore(Score fitness, Individual individual) {
        if (individual instanceof AbstractIndividual)
            ((AbstractIndividual) individual).setScore(fitness);
    }

}