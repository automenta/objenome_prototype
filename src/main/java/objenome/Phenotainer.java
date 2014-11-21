package objenome;

import objenome.dependency.Builder;
import objenome.gene.Parameterized;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The container (phenotype) generated by an individual Objenome
 */
public class Phenotainer extends Container {
    public final Map<Parameter, Object> parameterValues = new HashMap();
    public final Objenome objenome;
    public final Genetainer parent;

    public Phenotainer(Objenome o) {
        super(o.parentContext);        
        this.objenome = o;
        this.parent = o.parentContext;
        
        
        
        //remove all builders with ambiguosity
        List<String> toRemove = new ArrayList();
        for (Map.Entry<String, Builder> e : this.builders.entrySet()) {
            if (e.getValue() instanceof Parameterized)
                toRemove.add(e.getKey());
        }
        for (String s : toRemove) this.builders.remove(s);
        
        commit();
    }
    
    /** applies (current values of) the genes to the container for use by the next 
     *  instanced objects */
    public Phenotainer commit() {
        parameterValues.clear();
        for (Objene g : objenome.genes.values()) {
            g.apply(this);
        }
        return this;
    }
    
    public void use(Parameter p, Object value) {
        parameterValues.put(p, value);
    }

    public <T> T get(Parameter p) {
        return (T) parameterValues.get(p);
    }

    @Override
    public String toString() {
        return parameterValues + ",  " + constructorDependencies.toString() + ",  " + setterDependencies.toString() + ", ";
    }
    
    
}
