/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution;

import com.google.common.collect.Sets;
import java.util.Set;
import objenome.Phenotainer;
import objenome.problem.DevelopMethod;
import objenome.solve.Solution;

/**
 * Uses a dynamically generated expression to complete an abstract or interface method
 * TODO
 */
public class GPEvolveMethods implements Solution {
    
    public static String DYNAMIC_SUFFIX = "$$D";
    private final Set<DevelopMethod> methods;
    
    

    
    public GPEvolveMethods(DevelopMethod... m) {
        this.methods = Sets.newHashSet(m);
    }

    @Override
    public void apply(Phenotainer p) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    
    

    @Override
    public String key() {
        return "implement(" + methods.toString() + ")";
    }

    

    @Override
    public String toString() {
        return key();
    }

    public void addMethodToDevelop(DevelopMethod m) {
        methods.add(m);
    }
    
    
}
