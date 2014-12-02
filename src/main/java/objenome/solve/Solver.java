/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solve;

import java.util.Map;
import objenome.Genetainer;
import objenome.problem.Problem;

/**
 *
 * @author me
 */
public interface Solver {

    public void solve(Genetainer g, Map<Problem, Genetainer.Solution> p);
    
}
