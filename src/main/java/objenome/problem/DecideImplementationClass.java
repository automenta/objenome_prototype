/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.dependency;

import java.util.List;
import objenome.AbstractContainer;
import objenome.problem.Problem;

/**
 *
 * @author me
 */
public class DecideImplementationClass implements Problem, Builder {

    
    public final Class abstractClass;
    
    /** does not need to be unique, in which case repeats have higher probability of
        selection
    */
    public final List<Class> implementors;

    public DecideImplementationClass(Class abstractClass, List<Class> implementations) {
        this.abstractClass = abstractClass;
        this.implementors = implementations;
    }
    
    public int size() { return implementors.size(); }
    
    
    @Override
    public <T> T instance(AbstractContainer context) {
        if (implementors.size() == 1) {
            return (T) context.get(implementors.get(0) );
        }
        throw new RuntimeException(this + " must be disambiguated");
    }

    @Override
    public Class<?> type() {
        return abstractClass;
    }

    @Override
    public String toString() {
        return "MultiClassBuilder(" + implementors.toString() + ")";
    }
    
}