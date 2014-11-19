/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import java.lang.reflect.Parameter;
import java.util.List;
import objenome.Objene;
import objenome.Phenotainer;

abstract public class SetValue<X> extends Objene<X> {
    final Parameter p;

    public SetValue(Parameter p, List<Object> path, double initialValue) {
        super(path, initialValue);
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
}

