/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.impl;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import objenome.Builder;
import objenome.Context;
import objenome.Objene;
import objenome.Parameterized;

/**
 *
 * @author me
 */
public class MultiClassBuilder implements Builder, Parameterized {

    
    public final Class abstractClass;
    
    /** does not need to be unique, in which case repeats have higher probability of
        selection
    */
    public final List<Class> implementors;

    public MultiClassBuilder(Class abstractClass, List<Class> implementations) {
        this.abstractClass = abstractClass;
        this.implementors = implementations;
    }
    
    public int size() { return implementors.size(); }
    
    
    @Override
    public <T> T instance(Context context) {
        if (implementors.size() == 1) {
            return (T) context.get(implementors.get(0) );
        }
        throw new RuntimeException(this + " must be disambiguated");
    }

    @Override
    public Class<?> type() {
        return abstractClass;
    }

    /** stores a double value between 0...1.0 which is used to select equally
     *  from the list of classes in its creator Multiclass
     */
    public static class SelectClass extends Objene {
        
        public final MultiClassBuilder multiclass;

        public SelectClass(MultiClassBuilder multiclass) {
            super(Math.random());
            this.multiclass = multiclass;            
        }
        
        public Class getValue() {
            int num = multiclass.size();
            int which = (int)(doubleValue() * num);
            if (which == num) which = num-1;
            return multiclass.implementors.get(which);
        }

        @Override
        public String toString() {
            return "Class=" + getValue().toString();
        }
        
    }
    
    @Override
    public Collection<? extends Objene> getGenes() {
        return Lists.newArrayList(new SelectClass(this));
    }
    
}
