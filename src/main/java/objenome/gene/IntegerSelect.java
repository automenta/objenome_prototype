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
public class IntegerSelect extends Objene<Integer> {
    private int max;
    private int min;

    
    public IntegerSelect(Parameter p, List<Object> path, int defaultMin, int defaultMax) {
        this(path, defaultMin, defaultMax);        
        assert(p.getType() == int.class);

        Between between = p.getDeclaredAnnotation(Between.class);
        if (between!=null) {
            min = (int)between.min();
            max = (int)between.max();
        }
    }
    
    public IntegerSelect(List<Object> path, int min, int max) {
        super(path, Math.random());
        this.min = min;
        this.max = max;
    }
    
    public Integer getValue() {
        return (int)((doubleValue() * (max-min)) + min);
    }

    
    
}
