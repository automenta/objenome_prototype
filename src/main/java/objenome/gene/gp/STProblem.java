/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene.gp;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Map;

/**
 * Static-typed Problem provides a mechanism for setting default
 * configuration parameter values.
 */
public abstract class STProblem implements GPContainer.GPContainerAware {
    /**
     * The key for setting <code>Template</code> parameter.
     */
    public static final GPContainer.ConfigKey<STProblem> PROBLEM = new GPContainer.ConfigKey<STProblem>();
    /**
     * The key -&gt; value mapping.
     */
    private final HashMap<GPContainer.ConfigKey<?>, Object> properties = new HashMap<GPContainer.ConfigKey<?>, Object>(1);
    private GPContainer config;

    /**
     * Constructs a new <code>Template</code>.
     */
    public STProblem() {
    }

    @Override
    public void setConfig(GPContainer c) {
        this.config = c;        
        
        apply(config, properties);
        
    }

    /**  Enables default configuration template for generational executions. */
    public STProblem generates() {
        properties.put(Evolution.COMPONENTS, Lists.newArrayList(new Component[] {
            new Initialiser(),
            new FitnessEvaluator(),
            new GenerationalStrategy(new BranchedBreeder(), new FitnessEvaluator())            
        }));
        return this;
    }
    
    /**
     * Sets the default configuration parameters.
     *
     * @param template the default configuration parameters mapping.
     */
    protected abstract void apply(GPContainer c, Map<GPContainer.ConfigKey<?>, Object> template);

    /**
     * Retrieves the value of the configuration parameter associated with
     * the specified key.
     *
     * @param key the <code>ConfigKey</code> for the configuration parameter
     * to retrieve
     * @param defaultValue the default value to be returned if the parameter
     * has not been set
     * @return the value of the specified configuration parameter, or
     * <code>null</code> if it has not been set. The object type is defined
     * by the generic type of the key.
     */
    @SuppressWarnings(value = "unchecked")
    <T> T get(GPContainer.ConfigKey<T> key, T defaultValue) {
        T value = (T) properties.get(key);
        return (value == null) ? defaultValue : value;
    }
    
}
