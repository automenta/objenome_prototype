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
package objenome.evolve;

/**
 * An <code>AbstractIndividual</code> is a candidate solution with a settable
 * fitness value.
 *
 * @since 2.0
 */
public abstract class AbstractIndividual implements Individual {

    private static final long serialVersionUID = -4321760091640776785L;

    private Score score;

    /**
     * Sets this individual's fitness value
     *
     * @param score the fitness to set
     */
    public void setScore(Score score) {
        this.score = score;
    }

    /**
     * Returns the fitness value assigned to this individual
     *
     * @return a fitness value for this individual
     */
    @Override
    public Score score() {
        return score;
    }

    /**
     * Returns a clone of this individual with a copy of its fitness assigned
     *
     * @return an individual which is a copy of this individual
     */
    @Override
    public AbstractIndividual clone() {
        try {
            AbstractIndividual clone = (AbstractIndividual) super.clone();
            if (score != null) {
                clone.score = score.clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}