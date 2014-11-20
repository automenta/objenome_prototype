/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Boolean backed by a double, 0..0.5 = false, 0.5..1.0 = true
 */
public class BooleanSelect extends SetValue<Boolean> implements Numeric {
    
    public BooleanSelect(Parameter p, List<Object> path) {
        super(p, path, Math.random());        
        assert(p.getType() == boolean.class);
    }
    
    @Override
    public Boolean getValue() {
        return doubleValue() > 0.5;
    }

    @Override
    public Double getMin() {
        return 0d;
    }

    @Override
    public Double getMax() {
        return 1d;
    }

    @Override
    public Number getNumber() {
        return getValue() ? 1.0 : 0.0;
    }

    @Override
    public void setValue(double d) {
        set(d);
    }

    @Override
    public void mutate() {
        setValue( Math.random() * (getMax() - getMin()) + getMin() );
    }
    
}
