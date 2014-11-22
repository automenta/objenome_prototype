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

package objenome.gene.gp.epochx.event.stat;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import objenome.gene.gp.epochx.event.Event;
import objenome.gene.gp.epochx.event.EventManager;
import objenome.gene.gp.epochx.event.Listener;

/**
 * The <code>AbstractStat</code> represent the base class for classes that
 * gathers data and statistics about events. It also works as a central
 * repository for registering, removing and retrieving stat objects.
 * 
 * @see Event
 */
public abstract class AbstractStat<T extends Event> {

	/**
	 * An empty list of dependencies.
	 */
	public static final List<Class<? extends AbstractStat<?>>> NO_DEPENDENCIES = new ArrayList<Class<? extends AbstractStat<?>>>(
			0);

	/**
	 * The central repository of <code>AbstractStat</code> objects.
	 */
	private static final HashMap<Class<?>, Object> REPOSITORY = new HashMap<Class<?>, Object>();

	/**
	 * This is the stat listener. When the stat is registered, its listener is added to
	 * the {@link EventManager}.
	 */
	private Listener<T> listener = new Listener<T>() {

		@Override
		public void onEvent(T event) {
			AbstractStat.this.refresh(event);
		}
	};

	/**
	 * The event that trigger the stat to clear its values.
	 */
	private Class<Event> clearOnEvent;

	/**
	 * This is the clear listener. This is only created if the event to trigger the
	 * {@link #clear()} method is specified.
	 */
	private Listener<Event> clearOnListener;

	/**
	 * Constructs an <code>AbstractStat</code>.
	 */
	public AbstractStat() {
		this(NO_DEPENDENCIES);
	}

	/**
	 * Constructs an <code>AbstractStat</code>.
	 * 
	 * @param dependency the dependency of this stat.
	 */
	@SuppressWarnings("unchecked")
	public AbstractStat(Class<? extends AbstractStat<?>> dependency) {
		this(Arrays.<Class<? extends AbstractStat<?>>> asList(dependency));
	}

	/**
	 * Constructs an <code>AbstractStat</code>. The array of dependencies can be
	 * empty, in case this stat has no dependencies.
	 * 
	 * @param dependencies the array of dependencies of this stat.
	 */
	public AbstractStat(Class<? extends AbstractStat<?>> ... dependencies) {
		this(Arrays.asList(dependencies));
	}

	/**
	 * Constructs an <code>AbstractStat</code>. The list of dependencies can be
	 * empty, in case this stat has no dependencies.
	 * 
	 * @param dependencies the list of dependencies of this stat.
	 */
	public AbstractStat(List<Class<? extends AbstractStat<?>>> dependencies) {
		for (Class<? extends AbstractStat<?>> dependency: dependencies) {
			AbstractStat.register(dependency);
		}
	}

	/**
	 * Constructs an <code>AbstractStat</code>.
	 * 
	 * @param clearOn the event that trigger the stat to clear its values.
	 * @param dependency the dependency of this stat.
	 */
	@SuppressWarnings("unchecked")
	public <E extends Event> AbstractStat(Class<E> clearOn, Class<? extends AbstractStat<?>> dependency) {
		this(clearOn, Arrays.<Class<? extends AbstractStat<?>>> asList(dependency));
	}

	/**
	 * Constructs an <code>AbstractStat</code>. The array of dependencies can be
	 * empty, in case this stat has no dependencies.
	 * 
	 * @param clearOn the event that trigger the stat to clear its values.
	 * @param dependencies the array of dependencies of this stat.
	 */
	public <E extends Event> AbstractStat(Class<E> clearOn, Class<? extends AbstractStat<?>> ... dependencies) {
		this(clearOn, Arrays.asList(dependencies));
	}

	/**
	 * Constructs an <code>AbstractStat</code>. The list of dependencies can be
	 * empty, in case this stat has no dependencies.
	 * 
	 * @param clearOn the event that trigger the stat to clear its values.
	 * @param dependencies the list of dependencies of this stat.
	 */
	@SuppressWarnings("unchecked")
	public <E extends Event> AbstractStat(Class<E> clearOn, List<Class<? extends AbstractStat<?>>> dependencies) {
		for (Class<? extends AbstractStat<?>> dependency: dependencies) {
			AbstractStat.register(dependency);
		}

		Listener<E> trigger = new Listener<E>() {

			@Override
			public void onEvent(E event) {
				AbstractStat.this.clear();
			};
		};

		EventManager.getInstance().add(clearOn, trigger);

		clearOnEvent = (Class<Event>) clearOn;
		clearOnListener = (Listener<Event>) trigger;
	}

	/**
	 * Returns the class of the generic type T.
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getEvent() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Gathers the information about the event.
	 * 
	 * @param event the event
	 */
	public abstract void refresh(T event);

	/**
	 * Clears the cached values. This method is automatically called when a clear on event is specified.
	 */
	public void clear() {
	}

	/**
	 * Registers the specified <code>AbstractStat</code> in the repository, if
	 * it is not already registered.
	 * 
	 * @param type the class of <code>AbstractStat</code> to be registered.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Event, V extends AbstractStat<?>> void register(Class<V> type) {
		// if the repository already contains an instance of the specified stat,
		// we do not create a new one; otherwise, we create a new instance and
		// register its listener in the EventManager
		if (!REPOSITORY.containsKey(type)) {
			try {
				AbstractStat<E> stat = (AbstractStat<E>) type.newInstance();
				REPOSITORY.put(type, stat);
				EventManager.getInstance().add(stat.getEvent(), stat.listener);
			} catch (Exception e) {
				throw new RuntimeException("Could not create an instance of " + type, e);
			}
		}
	}

	/**
	 * Removes the specified <code>AbstractStat</code> from the repository.
	 * 
	 * @param type the class of <code>AbstractStat</code> to be removed.
	 */
	public static <E extends Event> void remove(Class<? extends AbstractStat<E>> type) {
		if (REPOSITORY.containsKey(type)) {
			AbstractStat<E> stat = type.cast(REPOSITORY.remove(type));
			EventManager.getInstance().remove(stat.getEvent(), stat.listener);
			EventManager.getInstance().remove(stat.clearOnEvent, stat.clearOnListener);
		}
	}

	/**
	 * Returns the <code>AbstractStat</code> object of the specified class. If
	 * the <code>AbstractStat</code> has been registered, it returns
	 * <code>null</code>.
	 * 
	 * @return the <code>AbstractStat</code> object of the specified class;
	 *         <code>null</code> if the <code>AbstractStat</code> has not been
	 *         registered.
	 */
	public static <V extends AbstractStat<?>> V get(Class<V> type) {
		return type.cast(REPOSITORY.get(type));
	}

	/**
	 * Removes all registered <code>AbstractStat</code> objects from the
	 * repository.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Event> void reset() {
		List<Class<?>> registered = new ArrayList<Class<?>>(REPOSITORY.keySet());

		for (Class<?> type: registered) {
			AbstractStat.remove((Class<? extends AbstractStat<E>>) type);
		}
	}
}