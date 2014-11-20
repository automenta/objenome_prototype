/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import java.util.ArrayList;
import java.util.List;
import objenome.gene.Numeric;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author me
 */
public class OptimizeUnivariateTest {
    
    public static class ExampleScalarFunction implements UnivariateFunction {
        
        private final double constParameter;

        public ExampleScalarFunction(@Between(min=-4.0, max=4.0) double constParameter) {
            
            this.constParameter = constParameter;
            System.out.println("c=" + constParameter);
        }
        
        
        public double value(double x) {
            //parabola with zero at (1,0)
            return Math.sin(constParameter * x) * (x-constParameter)*(x-constParameter);
        }

    }
    
    abstract public static class RootSolver implements Runnable {
        private Objenome objenome;
        private final List<Numeric> variables = new ArrayList();
        
        
        public RootSolver(Objenome o) {
            
            this.objenome = o;
            
            for (Objene g : o.getGenes()) {
                if (!(g instanceof Numeric))
                    throw new RuntimeException(this + " only applicable when objenome has only Numeric genes: " + o);
                variables.add( (Numeric) g );
            }
            
            if (variables.isEmpty())
                throw new RuntimeException(o + " has no numeric variables to solve");
        }
        
        @Override
        public void run() {
            if (variables.size() == 1) {
                
                BisectionSolver solver = new BisectionSolver();
                Numeric var = variables.get(0);                
                double best = solver.solve(1000, new UnivariateFunction() {
                    @Override public double value(final double d) {                        
                        
                        var.setValue(d);
                        
                        return score();
                    }                    
                }, var.getMin().doubleValue(), var.getMax().doubleValue());                             
                
                var.setValue(best);
                return;
            }
            else {
                throw new RuntimeException("Unknown solution for objenome " + objenome);
            }
            
        }
        
        protected double score() {
            objenome.commit();
            return score(objenome);
        }
        
        abstract double score(Objenome o);
                
    }
    
    @Test public void test1() {
        Genetainer g = new Genetainer(ExampleScalarFunction.class);
                
        Objenome o = g.genome(ExampleScalarFunction.class);
        
        new RootSolver(o) {
            
            @Override double score(Objenome o) {
         
                ExampleScalarFunction s = o.get(ExampleScalarFunction.class);
                
                //evaluate at 3 points
                double v = s.value(0.0) + s.value(0.5) + s.value(1.0);
                return v;
            }
            
        }.run();
        
        double bestParam = o.getGeneList().get(0).doubleValue();
        assertEquals(1.52592, bestParam, 0.001);
    }
}
