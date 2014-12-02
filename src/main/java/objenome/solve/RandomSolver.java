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
    public void solve(Genetainer g, Map<Problem, Genetainer.Solution> p) {
        for (Map.Entry<Problem, Genetainer.Solution> e : p.entrySet()) {
            Genetainer.Solution existingSolution = e.getValue();
            if (existingSolution == null) {
                e.setValue(new Genetainer.RandomSolution());
            }
        }
    }
    
}
