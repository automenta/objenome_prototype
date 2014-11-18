/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import java.lang.reflect.Parameter;
import java.util.List;
import objenome.Objene;

/**
 *
 * @author me
 */
public class BooleanSelect extends Objene {
    
    public BooleanSelect(Parameter p, List<Object> path) {
        this(path);        
        assert(p.getType() == boolean.class);
    }
    
    public BooleanSelect(List<Object> path) {
        super(path, Math.random());
    }
    
    public boolean getValue() {
        return doubleValue() > 0.5 ? true : false;
    }

}
