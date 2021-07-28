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

import junit.framework.TestCase;
import objenome.evolve.GP.GPKey;
import org.junit.Assert;


/**
 * The <code>ConfigTest</code> class provides unit tests for methods of the
 * {@link GP} class.
 * 
 * @see GP
 */
public class ConfigTest extends TestCase {

    GP config = new GP();
	/**
	 * Test for the {@link Config#get(ConfigKey)} method.
	 */
	public void testGet() {
		GPKey<Double> key = new GPKey<>();
		Assert.assertNull(config.get(key));
		config.set(key, 1.0);
		Assert.assertNotNull(config.get(key));
	}

	/**
	 * Test for the {@link Config#set(ConfigKey, Object)} method.
	 */
	public void testSet() {
		GPKey<Double> key = new GPKey<>();
		config.set(key, 0.2);
		Assert.assertEquals(Double.valueOf(0.2), config.get(key));

		config.set(key, 0.4);
		Assert.assertEquals(Double.valueOf(0.4), config.get(key));
	}

	/**
	 * Test for the {@link Config#reset()} method.
	 */
	public void testReset() {
		config.set(Population.SIZE, 100);		
		Assert.assertNotNull(config.get(Population.SIZE));

		config.clear();
		Assert.assertNull(config.get(Population.SIZE));
	}
}