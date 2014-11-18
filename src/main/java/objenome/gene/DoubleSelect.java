/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import objenome.Between;
import java.lang.reflect.Parameter;
import java.util.List;
import objenome.Objene;
import objenome.ObjosomeContext;

/**
 *
 * @author me
 */
public class DoubleSelect extends Objene<Double> {
    private double max;
    private double min;

    
    public DoubleSelect(Parameter p, List<Object> path, double defaultMin, double defaultMax) {
        this(path, defaultMin, defaultMax);        
        assert(p.getType() == double.class);

        Between between = p.getDeclaredAnnotation(Between.class);
        if (between!=null) {
            min = between.min();
            max = between.max();
        }
    }
    
    public DoubleSelect(List<Object> path, double min, double max) {
        super(path, Math.random());
        this.min = min;
        this.max = max;
    }
    
    @Override public void apply(ObjosomeContext c) { 
        
    }
    
    @Override
    public Double getValue() {
        return ((doubleValue() * (max-min)) + min);
    }

}
