/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solve;

import java.util.Map;
import objenome.Genetainer;
import objenome.problem.Problem;
import objenome.solve.Solver;

/**
 *
 * @author me
 */
public class RandomSolver implements Solver {

    @Override
    public void solve(Genetainer g, Map<Problem, Solution> p, Object[] targets) {
        for (Map.Entry<Problem, Solution> e : p.entrySet()) {
            Solution existingSolution = e.getValue();
            if (existingSolution == null) {
                e.setValue(new RandomSolution());
            }
        }
    }
    
}
