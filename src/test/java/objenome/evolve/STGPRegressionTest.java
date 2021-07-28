package objenome.evolve;

import junit.framework.TestCase;
import objenome.evolve.population.STGP;
import objenome.evolve.population.STGPRegression;
import objenome.util.BenchmarkSolutions;
import org.junit.Assert;
import org.junit.Test;

public class STGPRegressionTest extends TestCase {

	@Test public void testRegression() {            
            
            STGP e = new STGPRegression(
                    20, BenchmarkSolutions.XpXXpXXX);
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.best();
            
            //assertTrue(best.depth() > 1);            
            Assert.assertEquals(100, p.size());
            Assert.assertNotNull(p.best());

            
            System.out.println(p.best());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());
            
            
            
	}
}