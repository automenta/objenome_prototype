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
package objenome.gene.gp.event;

import junit.framework.TestCase;
import objenome.gene.gp.event.EventManager;
import objenome.gene.gp.event.Listener;
import objenome.gene.gp.event.RunEvent;

/**
 * The <code>EventManagerTest</code> class provides unit tests for methods of
 * the {@link EventManager} class.
 *
 * @see EventManager
 */
public class EventManagerTest extends TestCase {

    /**
     * Test for the {@link EventManager#reset()} method.
     */
    public void testReset() {
        EventManager manager = new EventManager();
        Listener<RunEvent> listener = new Listener<RunEvent>() {

            @Override
            public void onEvent(RunEvent event) {
            }
        };

        manager.add(RunEvent.class, listener);
        assertTrue(manager.remove(RunEvent.class, listener));

        manager.add(RunEvent.class, listener);
        manager.reset();
        assertFalse(manager.remove(RunEvent.class, listener));
    }
}
