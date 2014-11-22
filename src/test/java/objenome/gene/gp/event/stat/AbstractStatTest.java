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

package objenome.gene.gp.event.stat;

import junit.framework.TestCase;
import objenome.gene.gp.epochx.event.stat.AbstractStat;
import objenome.gene.gp.epochx.event.stat.RunBestFitness;

/**
 * The <code>AbstractStatTest</code> class provides unit tests for methods of the
 * {@link AbstractStat} class.
 * 
 * @see AbstractStat
 */
public class AbstractStatTest extends TestCase {

	/**
	 * Test for the {@link AbstractStat#reset()} method.
	 */
	public void testReset() {
		assertNull(AbstractStat.get(RunBestFitness.class));
		AbstractStat.register(RunBestFitness.class);
		assertNotNull(AbstractStat.get(RunBestFitness.class));

		AbstractStat.reset();
		assertNull(AbstractStat.get(RunBestFitness.class));
	}
}