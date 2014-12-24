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

import objenome.solver.evolve.GPContainer.GPKey;

/**
 * An implementation of this interface is used to generate a stream of numbers
 * with pseudo random qualities, in different data types.
 */
public interface RandomSequence {

    /**
     * The key for setting and retrieving the random number generator.
     */
    public static final GPKey<RandomSequence> RANDOM_SEQUENCE = new GPKey<>();

    /**
     * Gets the next <code>int</code> between <code>0</code> (inclusive) and
     * <code>n</code> (exclusive).
     *
     * @param n the upper limit of the generation
     * @return the next <code>int</code> in the pseudo random sequence
     */
    public int nextInt(int n);

    /**
     * Gets the next <code>int</code> between <code>Integer.MIN_VALUE</code>
     * (inclusive) and <code>Integer.MAX_VALUE</code> (inclusive).
     *
     * @return the next <code>int</code> in the pseudo random sequence
     */
    public int nextInt();

    /**
     * Gets the next <code>long</code> between <code>0</code> (inclusive) and
     * <code>n</code> (exclusive).
     *
     * @param n the upper limit of the generation
     * @return the next <code>long</code> in the pseudo random sequence
     */
    public long nextLong(long n);

    /**
     * Gets the next <code>long</code> between <code>Long.MIN_VALUE</code>
     * (inclusive) and <code>Long.MAX_VALUE</code> (inclusive).
     *
     * @return the next <code>long</code> in the pseudo random sequence
     */
    public long nextLong();

    /**
     * Gets the next <code>double</code> in the range <code>0.0</code>
     * (inclusive) and <code>1.0</code> (exclusive).
     *
     * @return the next <code>double</code> in the pseudo random sequence
     */
    public double nextDouble();

    /**
     * Gets the next <code>boolean</code> value.
     *
     * @return the next <code>true</code> or <code>false</code> value in the
     * pseudo random sequence
     */
    public boolean nextBoolean();

    /**
     * Sets the initial seed of the random number generation. Two instances of
     * the same implementation of <code>RandomSequence</code> given the same
     * seed, should produce the same sequence of numbers.
     *
     * @param seed the initial seed
     */
    public void setSeed(long seed);

}
