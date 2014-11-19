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
    
    public MultiClassBuilder any(Class abstractClass, Scope scope, Class<?>... klasses);
    
    //TODO: deduce common parent classes from a supplied list of classes:
    //default public MultiClassBuilder any(Class<?>... klasses) { ...
    
    
    
    default public Builder any(Class<?> abstractClass, Class<?>[] klasses) {
        if (klasses.length == 0)
            usable(abstractClass, abstractClass);
            
        int uniques = Sets.newHashSet(klasses).size();
        if (uniques == 1) {
            return use(abstractClass, klasses[0]);
        }
                
        return any(abstractClass, Scope.NONE, klasses);
    }

}
