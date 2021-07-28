package objenome.evolve;

import junit.framework.TestCase;
import objenome.problem.ProblemSTGP;
import objenome.problem.STGPRegression;
import objenome.solver.evolve.Population;
import objenome.solver.evolve.STGPIndividual;
import objenome.util.BenchmarkSolutions;
import org.junit.Assert;
import org.junit.Test;

public class STGPRegressionTest extends TestCase {

	@Test public void testRegression() {            
            
            ProblemSTGP e = new STGPRegression(20, BenchmarkSolutions.XpXXpXXX);
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.fittest();
            
            //assertTrue(best.depth() > 1);            
            Assert.assertEquals(100, p.size());
            Assert.assertNotNull(p.fittest());

            
            System.out.println(p.fittest());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());
            
            
            
	}
}