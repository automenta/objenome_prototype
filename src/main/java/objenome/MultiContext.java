/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import objenome.impl.MultiClassBuilder;

/**
 *
 * @author me
 */
public interface MultiContext extends ProtoContext {
    
    public MultiClassBuilder usable(Class abstractClass, Scope scope, Class<?>... klasses);
    
    default public MultiClassBuilder usable(Class abstractClass, Class<?>... klasses) {
        return usable(abstractClass, Scope.NONE, klasses);
    }

}
