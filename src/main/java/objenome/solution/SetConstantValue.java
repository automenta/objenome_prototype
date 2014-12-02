/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.List;
import objenome.Objene;
import objenome.Phenotainer;
import objenome.problem.DecideNumericValue;

abstract public class SetConstantValue<X> extends AtomicDouble implements Objene {
    
    public final DecideNumericValue problem;
    
    public SetConstantValue(DecideNumericValue p) {
        super();
        this.problem = p;
    }

    @Override public void apply(Phenotainer c) { 
        c.use(problem.parameter, getValue());
    }
        
    public List getPath() {
        return problem.path;
    }
    
    @Override
    public String key() {
        return getClass().getSimpleName() + "(" + problem.parameter.getDeclaringExecutable()+"|" + problem.parameter.getName() + ')';
    }    
    
    
    @Override
    public String toString() {
        Object lastPathElement = problem.path.get(problem.path.size()-1);        
        return lastPathElement + " => " + getValue();
    }        
    
    /** gets the data value */
    abstract public X getValue();
}

