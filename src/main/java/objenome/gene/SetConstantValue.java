/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import com.google.common.util.concurrent.AtomicDouble;
import java.lang.reflect.Parameter;
import java.util.List;
import objenome.Objene;
import objenome.Phenotainer;

abstract public class SetConstantValue<X> extends AtomicDouble implements Objene {
    final Parameter p;

    public final List<Object> path;

    public SetConstantValue(Parameter p, List<Object> path, double initialValue) {
        super(initialValue);
        this.path = path;
        this.p = p;
    }

    @Override public void apply(Phenotainer c) { 
        c.use(getParameter(), getValue());
    }
    
    public Parameter getParameter() {
        return p;
    }
    
    @Override
    public String key() {
        return getClass().getSimpleName() + "(" + p.getDeclaringExecutable()+"|" + p.getName() + ')';
    }    
    
    
    @Override
    public String toString() {
        Object lastPathElement = path.get(path.size()-1);        
        return lastPathElement + " => " + getValue();
    }        
    
    /** gets the data value */
    abstract public X getValue();
}

