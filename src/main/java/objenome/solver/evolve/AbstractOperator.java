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

import objenome.solver.evolve.event.Event;
import objenome.solver.evolve.event.EventManager;
import objenome.solver.evolve.event.Listener;
import objenome.solver.evolve.event.OperatorEvent;
import objenome.solver.evolve.event.OperatorEvent.EndOperator;
import objenome.solver.evolve.event.OperatorEvent.StartOperator;

/**
 * A skeletal implementation of the {@link Operator} interface than fires events
 * at the start (before) and end (after) the operator is performed. Typically,
 * subclasses will override one of the following methods:
 *
 * <ul>
 *
 * <li>{@link #perform(Individual...)}: when no custom end event is needed;
 *
 * <li>{@link #perform(OperatorEvent.EndOperator, Individual...)}: when a custom
 * end event is used, this method should be overridden to set the additional
 * information.
 *
 * </ul>
 *
 * @see Event
 * @see EventManager
 * @see Listener
 */
public abstract class AbstractOperator implements Operator {
    private GPContainer config;

    /**
     * override in subclasses
     */
    public void setConfig(GPContainer config) {

    }

    public GPContainer getConfig() {
        return config;
    }

    
    @Override
    public final Individual[] apply(Population population, Individual... individuals) {
        this.config = population.getConfig();
        setConfig(population.getConfig());

        Individual[] parents = clone(individuals);

        // fires the start event
        StartOperator start = getStartEvent(individuals);
        population.getConfig().fire(start);

        EndOperator end = getEndEvent(individuals);
        parents = perform(end, parents);

        // fires the end event only if the operator was successful
        if (parents != null) {
            end.setChildren(clone(parents));
            population.getConfig().fire(end);
        }

        return parents;
    }

    /**
     * Performs the operator on the specified individuals. If the operator is
     * not successful, the specified individuals will not be changed and
     * <code>null</code> is returned. The default implementation calls the
     * {@link #perform(Individual...)} method.
     * <p>
     * When overriding this method, the specified <code>EndOperator</code> event
     * can be used to provide more information about the operator. In order to
     * do so, the {@link #getEndEvent(Individual...)} method must return a
     * custom event instance, enabling this method to set its properties.
     * </p>
     *
     * @param event the end event object to be fired after this operator is
     * performed.
     * @param individuals the individuals undergoing the operator.
     *
     * @return the indivuals produced by this operator.
     *
     * @see #getEndEvent(Individual...)
     */
    public Individual[] perform(EndOperator event, Individual... individuals) {
        return perform(individuals);
    }

    /**
     * Performs the operator on the specified individuals. If the operator is
     * not successful, the specified individuals will not be changed and
     * <code>null</code> is returned. The default implementation just returns
     * the same individuals.
     *
     * @param individuals the individuals undergoing the operator.
     *
     * @return the indivduals produced by this operator; <code>null</code> when
     * the operator could not be applied.
     */
    public Individual[] perform(Individual... individuals) {
        return individuals;
    }

    /**
     * Returns the operator's start event. The default implementation returns a
     * <code>StartOperator</code> instance.
     *
     * @param parents the individuals undergoing the operator.
     *
     * @return the operator's start event.
     */
    protected StartOperator getStartEvent(Individual... parents) {
        return new StartOperator(this, parents);
    }

    /**
     * Returns the operator's end event. The default implementation returns a
     * <code>EndOperator</code> instance. The end event is passed to the
     * {@link #perform(Individual...)} method to allow the operator to add
     * additional information.
     *
     * @param parents the individuals undergoing the operator.
     *
     * @return the operator's end event.
     */
    protected EndOperator getEndEvent(Individual... parents) {
        return new EndOperator(this, parents);
    }

    /**
     * Returns a (deep) clone copy of the specified array of individuals.
     *
     * @param individuals the array of individuals to be cloned.
     *
     * @return a (deep) clone copy of the specified array of individuals.
     */
    private Individual[] clone(Individual[] individuals) {
        Individual[] clone = new Individual[individuals.length];

        for (int i = 0; i < clone.length; i++) {
            if (individuals[i] != null) {
                clone[i] = individuals[i].clone();
            }
        }

        return clone;
    }

}
