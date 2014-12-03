/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import objenome.Genetainer;
import objenome.Objene;
import objenome.Objenome;
import objenome.problem.DecideNumericValue;
import objenome.problem.Problem;
import objenome.solution.SetNumericValue;

/**
 *
 * @author me
 */
public abstract class NumericSolver<C> implements Solver  {
    
    public final Function<C, Double> function;
    public final Class<? extends C> model;

    public NumericSolver(Class<? extends C> model, Function<C, Double> function) {
        this.model = model;
        this.function = function;
        
    }
    
    abstract public void solve(Objenome o, List<SetNumericValue> variables);

    @Override
    public void solve(Genetainer g, Map<Problem, Solution> p, Object[] targets) {
        final List<SetNumericValue> variables = new ArrayList();
        
        //store backup in case it needs restored
        HashMap originalProblems = new HashMap(p);
        
        for (Map.Entry<Problem, Solution> e : p.entrySet()) {
            if (e.getValue() == null) {
                if (!(e.getKey() instanceof DecideNumericValue)) {
                    //wont be able to solve with non-numerical ambiguities
                    return;
                }
            }
       }
         
        for (Map.Entry<Problem, Solution> e : p.entrySet()) {
            if (e.getValue() == null) {
                if (e.getKey() instanceof DecideNumericValue) {
                    SetNumericValue v = ((DecideNumericValue)e.getKey()).newDefaultSetValue();
                    variables.add(v);
                    e.setValue(v);
                }
            }
        }
        
        if (variables.isEmpty()) {
            return;
        }
    
        
        Objenome o;
        try {
            o = g.genome(targets, p);
            solve(o, variables);
        } catch (Genetainer.IncompleteSolutionException ex) {
            p.clear();
            p.putAll(originalProblems);
        }
        
    }

    
    protected double eval(Objenome o) {
        return function.apply(o.get(model));
    }
    
    
    /** var = gene, from different casts */
    public double getMin(SetNumericValue var, Objene gene) {
        return var.getMin().doubleValue();
    }
    
    /** var = gene, from different casts */
    public double getMax(SetNumericValue var, Objene gene) {
        return var.getMax().doubleValue();
    }    
}
