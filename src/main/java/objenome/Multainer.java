/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import objenome.dependency.Builder;
import objenome.dependency.Scope;
import com.google.common.collect.Sets;
import com.sun.javafx.runtime.VersionInfo;
import java.util.List;
import java.util.Set;
import objenome.dependency.MultiClassBuilder;

/**
 * Non-determinate "Multi" Container
 */
public interface Multainer extends Prototainer {
    
    public MultiClassBuilder any(Class abstractClass, Scope scope, Class<?>... klasses);
    
    
    
        
    default public Builder any(Class<?> abstractClass, Class<?>[] klasses) {
        if (klasses.length == 0)
            usable(abstractClass, abstractClass);
            
        int uniques = Sets.newHashSet(klasses).size();
        if (uniques == 1) {
            return use(abstractClass, klasses[0]);
        }
                
        return any(abstractClass, Scope.NONE, klasses);
    }

    /** create all possible builders that can be made involving a set of classes 
     *  deduces common parent classes from a supplied list of classes
     *  TODO
     */
    default public List<Builder> possible(Set<Class<?>> classes) {
        return null;
    }
    
    /** create all possible builders that can be made involving the classes in a set of packages
     *  TODO
     */
    default public List<Builder> possible(String... packages) {
        //calls ReflectGraph, then Multainer.possible(classes)
        return null;
    }
}
