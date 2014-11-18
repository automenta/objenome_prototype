/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.collect.Sets;
import objenome.impl.MultiClassBuilder;

/**
 *
 * @author me
 */
public interface MultiContext extends ProtoContext {
    
    public MultiClassBuilder usable(Class abstractClass, Scope scope, Class<?>... klasses);
    
    //TODO: deduce common parent class from supplied classes:
    //default public MultiClassBuilder usable(Class<?>... klasses) { ...
    
    default public MultiClassBuilder usable(Class abstractClass, Class<?>... klasses) {
        if (klasses.length == 0)
            usable(abstractClass, abstractClass);
            
        int uniques = Sets.newHashSet(klasses).size();
        if (uniques == 1)
            usable(abstractClass, klasses[0]);
                
        return usable(abstractClass, Scope.NONE, klasses);
    }

}
