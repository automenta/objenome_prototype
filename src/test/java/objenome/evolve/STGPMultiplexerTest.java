package objenome.evolve;

import junit.framework.TestCase;
import objenome.evolve.population.STGPBoolean;
import objenome.util.BenchmarkSolutions;
import org.junit.Assert;
import org.junit.Test;

public class STGPMultiplexerTest extends TestCase {

	@Test public void testSTGPMultiplexer() {                                   
            
            STGPBoolean e = new STGPBoolean(BenchmarkSolutions.multiplexerProblem(6));
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.best();
            
            System.out.println(p.best());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());
            
            Assert.assertTrue(best.depth() > 3);
            Assert.assertEquals(100, p.size());
            Assert.assertNotNull(p.best());
            
            
            
		
		//final int noSuccess = getNoSuccesses(model, false, false);
	}
}