/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import objenome.Between;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 *
 * @author me
 */
public class DoubleSelect extends SetValue<Double> {
    private double max;
    private double min;

    
    public DoubleSelect(Parameter p, List<Object> path, double defaultMin, double defaultMax) {
        super(p, path, Math.random());        
        assert(p.getType() == double.class);

        this.min = defaultMin;
        this.max = defaultMax;
        
        Between between = p.getDeclaredAnnotation(Between.class);
        if (between!=null) {
            this.min = between.min();
            this.max = between.max();
        }
    }

    
    @Override
    public Double getValue() {
        return ((doubleValue() * (max-min)) + min);
    }

}
