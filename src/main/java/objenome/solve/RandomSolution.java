/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solve;

import objenome.Genetainer;
import objenome.Objene;
import objenome.problem.DecideNumericValue;
import objenome.problem.DevelopMethod;
import objenome.problem.Problem;
import objenome.solution.GPEvolveMethods;
import objenome.solution.SetBooleanValue;
import objenome.solution.SetDoubleValue;
import objenome.solution.SetImplementationClass;
import objenome.solution.SetIntegerValue;
import objenome.solution.dependency.DecideImplementationClass;

/**
 *
 * @author me
 */
public class RandomSolution implements Solution {
    private GPEvolveMethods gpEvolveMethods;

    @Override
    public Objene apply(Problem p) {
        if (p instanceof DecideNumericValue) {
            if (p instanceof DecideNumericValue.DecideBooleanValue) {
                return new SetBooleanValue((DecideNumericValue.DecideBooleanValue) p, Math.random() < 0.5);
            } else if (p instanceof DecideNumericValue.DecideIntegerValue) {
                return new SetIntegerValue((DecideNumericValue.DecideIntegerValue) p, Math.random());
            } else if (p instanceof DecideNumericValue.DecideDoubleValue) {
                return new SetDoubleValue((DecideNumericValue.DecideDoubleValue) p, Math.random());
            }
        } else if (p instanceof DecideImplementationClass) {
            return new SetImplementationClass((DecideImplementationClass) p, Math.random());
        } else if (p instanceof DevelopMethod) {
            if (gpEvolveMethods == null) {
                gpEvolveMethods = new GPEvolveMethods();
            }
            gpEvolveMethods.addMethodToDevelop((DevelopMethod) p);
            return gpEvolveMethods;
        }
        return null;
    }
    
}
