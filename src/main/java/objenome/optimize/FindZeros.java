/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.optimize;

import java.util.function.Function;
import objenome.Genetainer;
import objenome.Objene;
import objenome.Objenome;
import objenome.gene.Numeric;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;

/**
 * Find zeros of a scalar function within a range
 * Currently, the search ranges can be overriden by overriding getMin and getMax methods
 */
public class FindZeros<C> extends NumericSolver<C> {


    public FindZeros(Class<? extends C> model, Function<C, Double> function) {
        this(new Genetainer().genome(model), model, function);
    }
    
    public FindZeros(Objenome o, Class<? extends C> model, Function<C, Double> function) {
        super(o, model, function);
    }
    
    public Objenome run() {
        if (variables.size() == 1) {
            BisectionSolver solver = new BisectionSolver();
            //bind variables values to objenome
            
            Numeric var = variables.get(0);
            Objene gene = (Objene)var;
            
            double best = solver.solve(1000, new UnivariateFunction() {
                @Override
                public double value(final double d) {
                    var.setValue(d);
                    return eval();
                }
            }, getMin(var, gene), getMax(var, gene)); //var.getMin().doubleValue(), var.getMax().doubleValue());
            var.setValue(best);
            return objenome;
        } else {
            throw new RuntimeException("Unknown how to solve objenome " + objenome);
        }
        
    }

}
