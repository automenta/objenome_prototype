/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.collect.Sets;
import objenome.impl.MultiClassBuilder;

/**
 * Non-determinate "Multi" Container
 */
public interface Multainer extends Prototainer {
    
    public MultiClassBuilder usable(Class abstractClass, Scope scope, Class<?>... klasses);
    
    //TODO: deduce common parent classes from a supplied list of classes:
    //default public MultiClassBuilder usable(Class<?>... klasses) { ...
    
    default public MultiClassBuilder usable(Class<?> abstractClass, Class<?>... klasses) {
        if (klasses.length == 0)
            usable(abstractClass, abstractClass);
            
        int uniques = Sets.newHashSet(klasses).size();
        if (uniques == 1)
            usable(abstractClass, klasses[0]);
                
        return usable(abstractClass, Scope.NONE, klasses);
    }

}
