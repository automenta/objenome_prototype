package objenome.evolve;

import objenome.evolve.Population;
import objenome.evolve.STGPIndividual;
import junit.framework.TestCase;
import objenome.evolve.problem.STGPMultiplexer;
import org.junit.Test;

public class STGPMultiplexerTest extends TestCase {

	@Test public void testSTGPMultiplexer() {                                   
            
            STGPMultiplexer e = new STGPMultiplexer(6);
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.fittest();
            
            /*System.out.println(p.fittest());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());*/
            
            assertTrue(best.depth() > 3);            
            assertEquals(100, p.size());
            assertNotNull(p.fittest());
            
            
            
		
		//final int noSuccess = getNoSuccesses(model, false, false);
	}
}