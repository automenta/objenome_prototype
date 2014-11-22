package objenome.gene.gp;

import junit.framework.TestCase;
import objenome.gene.gp.benchmark.STGPMultiplexer6Bit;
import static objenome.gene.gp.Config.Template.TEMPLATE;
import org.junit.Test;

public class STGPIndividualTest extends TestCase {

	@Test public void testEmpty() {                                   
            
            EvolutionGenerator e = new EvolutionGenerator();
            e.set(TEMPLATE, new STGPMultiplexer6Bit());
            
            Population p = e.run();
            STGPIndividual best = (STGPIndividual)p.fittest();
            
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