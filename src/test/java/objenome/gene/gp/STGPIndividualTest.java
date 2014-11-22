package objenome.gene.gp;

import junit.framework.TestCase;
import objenome.gene.gp.benchmark.STGPMultiplexer6Bit;
import objenome.gene.gp.epochx.Config;
import static objenome.gene.gp.epochx.Config.Template.TEMPLATE;
import objenome.gene.gp.epochx.Evolver;
import objenome.gene.gp.epochx.Population;
import org.junit.Test;

public class STGPIndividualTest extends TestCase {

	@Test public void testEmpty() {
            
            Config.getInstance().set(TEMPLATE, new STGPMultiplexer6Bit());
            
            Evolver evolver = new Evolver();
            
            Population p = evolver.run();
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