/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import java.util.List;
import objenome.Objene;
import objenome.impl.MultiClassBuilder;

/** stores a double value between 0...1.0 which is used to select equally
 *  from the list of classes in its creator Multiclass
 */
public class ClassSelect extends Objene<Class> {
    public final MultiClassBuilder multiclass;

    public ClassSelect(List<Object> path, MultiClassBuilder multiclass) {
        super(path, Math.random());
        this.multiclass = multiclass;
    }

    @Override
    public Class getValue() {
        int num = multiclass.size();
        int which = (int) (doubleValue() * num);
        if (which == num) {
            which = num - 1;
        }
        return multiclass.implementors.get(which);
    }

    @Override
    public String toString() {
        return "Class=" + getValue().toString();
    }
    
}
