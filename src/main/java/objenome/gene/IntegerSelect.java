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
public class IntegerSelect extends SetValue<Integer> implements Numeric {
    private int max;
    private int min;
    
    public IntegerSelect(Parameter p, List<Object> path, int defaultMin, int defaultMax) {
        super(p, path, Math.random());
        //assert(p.getType() == int.class);
        this.min = defaultMin;
        this.max = defaultMax;
        
        Between between = p.getDeclaredAnnotation(Between.class);
        if (between!=null) {
            min = (int)between.min();
            max = (int)between.max();
        }
    }
        
    @Override
    public Integer getValue() {
        return intValue();
            //((doubleValue() * (max-min)) + min);
    }

    @Override
    public Integer getMin() {
        return min;
    }

    @Override
    public Integer getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public void setValue(double d) {
        this.set(d);
    }

    
    @Override
    public Number getNumber() {
        return getValue();
    }
    
    @Override
    public void mutate() {
        setValue( Math.random() * (getMax() - getMin()) + getMin() );
    }
    
}
