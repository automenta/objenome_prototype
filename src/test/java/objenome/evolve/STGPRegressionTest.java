package objenome.evolve;

import junit.framework.TestCase;
import objenome.evolve.problem.STGPRegression;
import objenome.evolve.tools.BenchmarkSolutions;
import org.junit.Test;

public class STGPRegressionTest extends TestCase {

	@Test public void testRegression() {            
            
            ProblemSTGP e = new STGPRegression(BenchmarkSolutions.XpXXpXXX);
            
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