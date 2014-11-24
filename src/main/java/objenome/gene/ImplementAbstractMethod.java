/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

import java.lang.reflect.Method;
import objenome.Objene;
import objenome.Phenotainer;

/**
 * Uses a dynamically generated expression to complete an abstract or interface method
 * TODO
 */
public class ImplementAbstractMethod implements Objene {
    public static String DYNAMIC_SUFFIX = "$$D";
    
    public final Method method;

    public ImplementAbstractMethod(Method m) {
        this.method = m;
    }

    public Class type() {
        return method.getDeclaringClass();
    }
    
    @Override
    public void apply(Phenotainer c) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String key() {
        return "implement(" + method.toString() + ")";
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
