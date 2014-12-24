package objenome.evolve;

import objenome.solver.evolve.STGPIndividual;
import objenome.solver.evolve.Population;
import junit.framework.TestCase;
import objenome.problem.Observation;
import objenome.problem.STGPFunctionApproximation;
import org.junit.Test;

public class STGPFunctionApproximationTest extends TestCase {

	@Test public void testRegression() {            
            
            STGPFunctionApproximation e = new STGPFunctionApproximation(100);
            
            //setup function
            for (double x = 0; x < 4.0; x++) {
                e.samples.add(new Observation<Double[], Double>( 
                        new Double[] { x },
                        1.0 / (1.0 + Math.sin(x))
                ));
            }
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.fittest();
            
            //assertTrue(best.depth() > 1);            
            assertEquals(100, p.size());
            assertNotNull(p.fittest());

            
            System.out.println(p.fittest());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());
            
            
            
	}
}