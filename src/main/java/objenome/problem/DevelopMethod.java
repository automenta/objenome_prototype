/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.problem;

import java.lang.reflect.Method;

/**
 *
 * @author me
 */
public class DevelopMethod implements Problem {
    public final Method method;

    public DevelopMethod(Method unimplementedAbstractMethod) {
        this.method = unimplementedAbstractMethod;
    }
    
}
