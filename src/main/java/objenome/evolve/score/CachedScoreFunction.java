/* 
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
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
package objenome.evolve.score;

import objenome.evolve.AbstractScorer;
import objenome.evolve.Individual;
import objenome.evolve.Population;
import objenome.evolve.Score;

import java.util.HashMap;
import java.util.Map;

/**
 * A fitness function which caches fitness scores and delegates fitness
 * calculations to a delegate fitness function. For the caching to work
 * correctly the fitness cases must not be changed, or the cache must be cleared
 * when they are changed. Caching is based on hash code so it is important that
 * Individuals have a suitable hashCode implementation.
 *
 * @since 2.0
 */
public class CachedScoreFunction extends AbstractScorer {

    // The cache of fitness scores
    private final Map<Object, Score> cache;

    // The fitness function to delegate to when not in cache
    private final AbstractScorer delegate;

    /**
     * Constructs a <code>CachedFitnessFunction</code> fitness function. Fitness
     * calculations are performed by the given delegate, unless the individual's
     * fitness has been cached.
     *
     * @param delegate the fitness function the fitness calculations should be
     * delegated to
     */
    public CachedScoreFunction(AbstractScorer delegate) {
        this.delegate = delegate;
        cache = new HashMap<>();
    }

    /**
     * Returns the fitness of the given individual. If the cache contains a
     * fitness score for the given individual then the fitness value from the
     * cache is returned. Otherwise the fitness is calculated using the delegate
     * fitness function and then cached for future evaluations.
     *
     * The caching of individuals is based on hash codes. If two individuals
     * return the same hash code then its assumed they have equal fitness
     * scores.
     *
     * @param individual the program to evaluate
     * @return the fitness of the given individual
     */
    @Override
    public Score evaluate(Population population, Individual individual) {
        Object key = individual.hashCode();

        //TODO Use source generator if one is set
        Score fitness = cache.get(key);
        if (fitness == null) {
            fitness = delegate.evaluate(population, individual);
            cache.put(key, fitness);
        }

        return fitness;
    }

    /**
     * Clears the cache.
     */
    public void clear() {
        cache.clear();
    }

}