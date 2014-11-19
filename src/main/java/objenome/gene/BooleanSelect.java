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

/**
 *
 * @author me
 */
public class BooleanSelect extends SetValue<Boolean> {
    
    public BooleanSelect(Parameter p, List<Object> path) {
        super(p, path, Math.random());        
        assert(p.getType() == boolean.class);
    }
    
    @Override
    public Boolean getValue() {
        return doubleValue() > 0.5;
    }


    
}
