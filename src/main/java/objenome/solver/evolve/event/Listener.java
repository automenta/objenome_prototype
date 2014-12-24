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
package objenome.solver.evolve.event;

/**
 * The <code>Listener</code> interface implemented by any class whose instances
 * are intended to receive notification about events generated during the
 * execution of a run.
 *
 * @param <T> the type of events that this object will listen to
 *
 * @see Event
 * @see EventManager
 */
public interface Listener<T extends Event> {

    /**
     * This method is called every time an event of type <code>T</code> is
     * fired.
     *
     * @param event the fired event object.
     */
    public void onEvent(T event);

}
