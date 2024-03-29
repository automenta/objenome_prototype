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
package objenome.evolve.selection;

import objenome.evolve.*;
import objenome.evolve.score.DoubleScore;

/**
 * This class represents an {@link IndividualSelector} that selects individuals
 * with a probability proportional to their fitness. In order to calculate a
 * probability, individuals must have a {@link DoubleScore} value.
 */
public class RouletteSelector extends AbstractSelector {

    /**
     * The individuals' selection probabilities.
     */
    private double[] roulette;

    /**
     * Compute the individuals' selection probabilities.
     *
     * @param population the current population.
     */
    @Override
    public void setup(Population population) {
        Score best = population.get(0).score();
        Score worst = best;

        if (!(best instanceof DoubleScore)) {
            throw new IllegalArgumentException("Fitness not supported: " + best.getClass());
        }

        roulette = new double[population.size()];
        double total = 0.0;

        for (int i = 0; i < population.size(); i++) {
            Score fitness = population.get(i).score();
            if (fitness.compareTo(best) > 0) {
                best = fitness;
            } else if (fitness.compareTo(worst) < 0) {
                worst = fitness;
            }

            roulette[i] = ((DoubleScore) fitness).getValue();
            total += roulette[i];
        }

        double bestValue = ((DoubleScore) best).getValue();
        double worstValue = ((DoubleScore) worst).getValue();

        // invert if minimising - using adjusted fitness.
        if (bestValue < worstValue) {
            total = 0.0;
            double delta = (bestValue < 0) ? Math.abs(bestValue) : 0.0;
            for (int i = 0; i < population.size(); i++) {
                roulette[i] = 1 / (1 + delta + roulette[i]);
                total += roulette[i];
            }
        }

        // normalise roulette values and accumulate.
        double cumulative = 0.0;
        for (int i = 0; i < population.size(); i++) {
            roulette[i] = cumulative + (roulette[i] / total);
            cumulative = roulette[i];
        }
        roulette[population.size() - 1] = 1.0;

        super.setup(population);
    }

    /**
     * Returns an individual using the fitness proportionate selection strategy.
     *
     * @return an individual using the fitness proportionate selection strategy.
     */
    @Override
    public Individual select() {
        double random = ((RandomSequence)population.getConfig().get(RandomSequence.RANDOM_SEQUENCE)).nextDouble();

        for (int i = 0; i < roulette.length; i++) {
            if (random < roulette[i]) {
                return population.get(i);
            }
        }

        throw new IllegalStateException("Illegal roulette probabilities");
    }

}