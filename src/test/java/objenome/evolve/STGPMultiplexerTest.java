package objenome.evolve;

import objenome.solver.evolve.STGPIndividual;
import objenome.solver.evolve.Population;
import junit.framework.TestCase;
import objenome.problem.STGPBoolean;
import objenome.util.BenchmarkSolutions;
import org.junit.Test;

public class STGPMultiplexerTest extends TestCase {

	@Test public void testSTGPMultiplexer() {                                   
            
            STGPBoolean e = new STGPBoolean(BenchmarkSolutions.multiplexerProblem(6));
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.fittest();
            
            System.out.println(p.fittest());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());
            
            assertTrue(best.depth() > 3);            
            assertEquals(100, p.size());
            assertNotNull(p.fittest());
            
            
            
		
		//final int noSuccess = getNoSuccesses(model, false, false);
	}
}