/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solve;

import java.util.Arrays;
import objenome.Genetainer;
import objenome.problem.Problem;

/**
 *
 * @author me
 */
public class IncompleteSolutionException extends Exception {

    public IncompleteSolutionException(Iterable<Problem> p, Object[] keys, Genetainer g) {
        super("Missing solution(s) for " + p + " to build " + Arrays.toString(keys) + " in " + g + ": " + g);
    }
    
}
