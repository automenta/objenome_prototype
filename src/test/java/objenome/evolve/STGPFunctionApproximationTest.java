package objenome.evolve;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import objenome.solver.evolve.STGPIndividual;
import objenome.solver.evolve.Population;
import junit.framework.TestCase;
import objenome.problem.Observation;
import objenome.problem.STGPFunctionApproximation;
import objenome.solver.evolve.Individual;
import org.junit.Test;

public class STGPFunctionApproximationTest extends TestCase {

	@Test public void testRegression() {            
            
            int individuals = 50;
            
            STGPFunctionApproximation e = new STGPFunctionApproximation(individuals);
            
            //setup function
            for (double x = 0; x < 4.0; x+=0.1) {
                e.samples.add(new Observation<Double[], Double>( 
                        new Double[] { x },
                        1.0 / (1.0 + Math.sin(x))
                ));
            }
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.fittest();
            
            //assertTrue(best.depth() > 1);            
            assertEquals(individuals, p.size());
            assertNotNull(p.fittest());

            List<Individual>  firstBest = Lists.newArrayList(p.elites(0.5f));
            
            System.out.println(p.fittest());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());
                        
            p.cullThis(0.25f);
            
            //System.out.println(p.size());
            
            assertTrue(p.size() < (individuals * 0.8));
            
            p = e.run();
            
            assertEquals(individuals, p.size());
            
            for (int i = 0; i < 3; i++) {
                p.cullThis(0.8f);
                p = e.run();
            }
            
            List<Individual> nextBest = Lists.newArrayList(p.elites(0.5f));
            
            /*System.out.println(firstBest);
            System.out.println(nextBest);*/
            
            //show some evolution in change of elites
            assertTrue(!firstBest.equals(nextBest));
            
	}
}