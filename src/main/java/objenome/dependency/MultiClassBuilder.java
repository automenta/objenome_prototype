/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.dependency;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import objenome.AbstractContainer;
import objenome.Objene;
import objenome.gene.Parameterized;
import objenome.gene.SelectImplementation;

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
    public Collection<? extends Objene> getGenes(List<Object> path) {
        /*if (implementors.size() == 1)
            return Collections.EMPTY_LIST;*/
        
        return Lists.newArrayList(new SelectImplementation(path, this));
    }

    @Override
    public String toString() {
        return "MultiClassBuilder(" + implementors.toString() + ")";
    }
    
    
    
}
