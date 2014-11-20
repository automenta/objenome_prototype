/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import objenome.optimize.FindZeros;
import java.util.function.Function;
import objenome.optimize.OptimizeMultivariate;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author me
 */
public class OptimizeUnivariateTest {
    
    public static class ExampleScalarFunction  {
        
        private final double constParameter;

        public ExampleScalarFunction(@Between(min=-4.0, max=4.0) double constParameter) {
            
            this.constParameter = constParameter;
        }
        
        
        public double output(double x) {
            return Math.sin(constParameter * x) * (x-constParameter)*(x-constParameter);
        }

    }
    
    public static class ExampleMultivariateFunction  {
        
        private final double a;
        private final boolean b;

        public ExampleMultivariateFunction(@Between(min=-4.0, max=4.0) double a, boolean b) {
            
            this.a = a;
            this.b = b;
        }
        
        
        public double output(double x) {
            if (b)
                return Math.sin(a * x) * (x-a)*(x-a);
            else
                return Math.tanh(a * x) * (x-a)*(x-a);
        }

    }    
    
    @Test public void testFindZeros() {
        Genetainer g = new Genetainer(ExampleScalarFunction.class);
                
        Objenome o = g.genome(ExampleScalarFunction.class);
        
        new FindZeros(o, ExampleScalarFunction.class, new Function<ExampleScalarFunction, Double>() {
            public Double apply(ExampleScalarFunction s) {                
                return s.output(0.0) + s.output(0.5) + s.output(1.0);
            }
        }).run();
        
        double bestParam = o.getGeneList().get(0).doubleValue();
        assertEquals(1.52592, bestParam, 0.001);
    }
    
    @Test public void testMultivariate() {
        Genetainer g = new Genetainer(ExampleMultivariateFunction.class);
                
        Objenome o = g.genome(ExampleMultivariateFunction.class);
        
        new OptimizeMultivariate(o, ExampleMultivariateFunction.class, new Function<ExampleMultivariateFunction, Double>() {
            public Double apply(ExampleMultivariateFunction s) {                                
                return s.output(0.0) + s.output(0.5) + s.output(1.0);
            }
        }).run();
        
        //double bestParam = o.getGeneList().get(0).doubleValue();
        System.out.println(o.genes);
        //assertEquals(1.52592, bestParam, 0.001);
    }
    
}
