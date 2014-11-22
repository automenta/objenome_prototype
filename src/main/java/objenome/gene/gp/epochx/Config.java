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

package objenome.gene.gp.epochx;

import java.util.HashMap;
import java.util.Map;

import objenome.gene.gp.epochx.event.ConfigEvent;
import objenome.gene.gp.epochx.event.EventManager;

/**
 * The <code>Config</code> class provides a centralised store for configuration
 * parameters. It uses a singleton which is obtainable with the
 * <code>getInstance</code> method. Each parameter is referenced with a
 * {@link ConfigKey} which is used to both set new parameters and retrieve
 * existing parameter values. the key also constrains the data-type of the
 * parameter value with its generic type.
 * 
 * @see ConfigKey
 */
public class Config {

	/**
	 * The singleton instance.
	 */
	private static final Config singleton = new Config();

	/**
	 * The key -&gt; value mapping.
	 */
	private final HashMap<ConfigKey<?>, Object> mapping = new HashMap<ConfigKey<?>, Object>();

	/**
	 * No instance are allowed, appart from the singleton.
	 * 
	 */
	private Config() {
	}

	/**
	 * Returns the singleton <code>Config</code> instance.
	 * 
	 * @return the singleton <code>Config</code> instance
	 */
	public static Config getInstance() {
		return singleton;
	}

	/**
	 * Sets the value of the specified configuration key. If the given key
	 * already has a value associated with it, then it will be overwritten. The
	 * value is constrained to be of the correct object type as defined by the
	 * generic type of the key. Calling this method will trigger the firing of a
	 * new configuration event after the configuration option has been set.
	 * 
	 * @param key the <code>ConfigKey</code> for the configuration parameter
	 *        that a new value is to be set for
	 * @param value the new value to set for the specified configuration key
	 */
	public <T> void set(ConfigKey<T> key, T value) {
		mapping.put(key, value);
		EventManager.getInstance().fire(new ConfigEvent(key));
	}

	/**
	 * Retrieves the value of the configuration parameter associated with the
	 * specified key. If no value has been set for the given key then
	 * <code>null</code> is returned.
	 * 
	 * @param key the <code>ConfigKey</code> for the configuration parameter
	 *        to retrieve
	 * @return the value of the specified configuration parameter, or
	 *         <code>null</code> if it has not been set. The object type is
	 *         defined by the generic type of the key.
	 */
	public <T> T get(ConfigKey<T> key) {
		return get(key, null);
	}

	/**
	 * Retrieves the value of the configuration parameter associated with the
	 * specified key. If the parameter has not been set, the value will be
	 * retrived from the <code>Template</code> object.
	 * 
	 * @param key the <code>ConfigKey</code> for the configuration parameter
	 *        to retrieve
	 * @param defaultValue the default value to be returned if the parameter
	 *        has not been set
	 * @return the value of the specified configuration parameter, or
	 *         <code>null</code> if it has not been set. The object type is
	 *         defined by the generic type of the key.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(ConfigKey<T> key, T defaultValue) {
		T value = (T) mapping.get(key);

		if (value == null) {
			Template template = (Template) mapping.get(Template.TEMPLATE);
			return (template == null) ? defaultValue : template.get(key, defaultValue);
		}

		return value;
	}
	
	/**
	 * This method was originally designed to initialize the default values. The preferred
	 * method is to specify a {@link Template} object using the {@link #set(ConfigKey, Object)}.
	 * Calls to this method will initialize the configuration with the {@link GenerationalTemplate}. 
	 */
	@Deprecated
	public void defaults() {
		set(Template.TEMPLATE, new GenerationalTemplate());
	}

	/**
	 * Removes all configuration parameter mapping. The configuration will be
	 * empty this call returns.
	 */
	public void reset() {
		mapping.clear();
	}

	/**
	 * Instances of <code>ConfigKey</code> are used to uniquely identify
	 * configuration parameters. The generic type <code>T</code> defines a
	 * constraint upon the object type of the parameter's value.
	 * 
	 * @param <T> the required object type of values for this parameter
	 */
	public static class ConfigKey<T> {}

	/**
	 * The <code>Template</code> class provides a mechanism for setting default
	 * configuration parameter values.
	 */
	public abstract static class Template {

		/**
		 * The key for setting <code>Template</code> parameter.
		 */
		public final static ConfigKey<Template> TEMPLATE = new ConfigKey<Template>();

		/**
		 * The key -&gt; value mapping.
		 */
		private final HashMap<ConfigKey<?>, Object> template = new HashMap<ConfigKey<?>, Object>(1);

		/**
		 * Constructs a new <code>Template</code>.
		 */
		public Template() {
			fill(template);
		}

		/**
		 * Sets the default configuration parameters.
		 * 
		 * @param template the default configuration parameters mapping.
		 */
		protected abstract void fill(Map<ConfigKey<?>, Object> template);

		/**
		 * Retrieves the value of the configuration parameter associated with the
		 * specified key.
		 * 
		 * @param key the <code>ConfigKey</code> for the configuration parameter
		 *        to retrieve
		 * @param defaultValue the default value to be returned if the parameter
		 *        has not been set
		 * @return the value of the specified configuration parameter, or
		 *         <code>null</code> if it has not been set. The object type is
		 *         defined by the generic type of the key.
		 */
		@SuppressWarnings("unchecked")
		private <T> T get(ConfigKey<T> key, T defaultValue) {
			T value = (T) template.get(key);
			return (value == null) ? defaultValue : value;
		}
	}
}