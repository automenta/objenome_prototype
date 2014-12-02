/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution;

import objenome.problem.Between;
import objenome.problem.DecideNumericValue.DecideDoubleValue;

/**
 *
 * @author me
 */
public class SetDoubleValue extends SetConstantValue<Double> implements Numeric {
    
    
    public SetDoubleValue(DecideDoubleValue d, @Between(min=0, max=1) double normalizedValue) {
        super(d);        
        setNormalizedValue(normalizedValue);        
    }

    @Override
    public Double getMin() {
        return ((DecideDoubleValue)problem).min;
    }

    @Override
    public Double getMax() {
        return ((DecideDoubleValue)problem).max;
    }

    public void setNormalizedValue(@Between(min=0, max=1) double v) {
        setValue(v * (getMax() - getMin()) + getMin());
    }
    
    @Override
    public void setValue(double d) {
        this.set(d);
    }
    
    @Override
    public Double getValue() {
        return doubleValue(); //((doubleValue() * (max-min)) + min);
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
