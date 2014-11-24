/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import java.util.List;
import objenome.Phenotainer;
import objenome.dependency.MultiClassBuilder;

/** stores a double value between 0...N which is used to select equally
 *  from the list of N classes in its creator Multiclass
 */
public class SelectImplementation extends SetConstantValue<Class> implements Numeric {
    public final MultiClassBuilder multiclass;

    public SelectImplementation(List<Object> path, MultiClassBuilder multiclass) {
        super(null, path, Math.random());
        this.multiclass = multiclass;
    }

    @Override
    public Class getValue() {
        int num = multiclass.size();
        int which = (int) (doubleValue()*multiclass.size());
        if (which == num) {
            which = num - 1;
        }
        return multiclass.implementors.get(which);
    }

    @Override public void apply(Phenotainer c) { 
        c.remove(multiclass.abstractClass);
        c.use(multiclass.abstractClass, getValue());
    }

    @Override
    public String toString() {
        return "ClassSelect(" + multiclass.toString() +") => " + getValue();
    }

    @Override
    public String key() {
        //Path?
        return "ClassSelect("+multiclass.abstractClass+')';
    }

    @Override
    public Double getMin() {
        return 0.0d;
    }

    @Override
    public Double getMax() {
        return 1.0d;
    }

    @Override
    public Number getNumber() {
        return doubleValue();
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
