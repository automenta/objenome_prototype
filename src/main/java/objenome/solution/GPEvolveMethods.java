/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution;

import objenome.problem.DevelopMethod;
import java.lang.reflect.Method;
import objenome.Objene;
import objenome.Phenotainer;
import objenome.problem.Problem;

/**
 * Uses a dynamically generated expression to complete an abstract or interface method
 * TODO
 */
public class GPEvolveMethods implements Objene {
    public static String DYNAMIC_SUFFIX = "$$D";
    private final DevelopMethod[] methods;
    
    

    
    public GPEvolveMethods(DevelopMethod... m) {
        this.methods = m;
    }

    
    @Override
    public void apply(Phenotainer c) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String key() {
        return "implement(" + methods.toString() + ")";
    }

    @Override
    public void mutate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return key();
    }
    
    
}
