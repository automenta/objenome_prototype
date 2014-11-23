package objenome.gene.gp;

import junit.framework.TestCase;
import objenome.gene.gp.problem.STGPMultiplexer;
import org.junit.Test;

public class STGPIndividualTest extends TestCase {

	@Test public void testSTGPMultiplexer() {                                   
            
            //Evolution e = new Evolution<STGPIndividual>().solve(new STGPMultiplexer(6));
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