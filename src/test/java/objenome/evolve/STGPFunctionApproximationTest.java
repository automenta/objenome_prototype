package objenome.evolve;

import com.google.common.collect.Lists;
import junit.framework.TestCase;
import objenome.evolve.population.STGPFunctionApproximation;
import objenome.util.Observation;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class STGPFunctionApproximationTest extends TestCase {

	@Test public void testRegression() {            
            
            int individuals = 50;
            
            STGPFunctionApproximation e = new STGPFunctionApproximation(
                    individuals, 3, true, true, true, true);
            
            //setup function
            int j =0;
            for (double x = 0; x < 4.0; x+=0.1) {
                e.samples.add(new Observation<>(
                        new Double[]{x},
                        (j ^ (j + 1000)) * (Math.sin(x))
                ));
                j++;
            }
            
            Population<STGPIndividual> p = e.run();
            
            STGPIndividual best = p.best();
            
            //assertTrue(best.depth() > 1);            
            Assert.assertEquals(individuals, p.size());
            Assert.assertNotNull(p.best());

            List<Individual>  firstBest = Lists.newArrayList(p.elites(0.5f));
            
            System.out.println(p.best());
            System.out.println(p.size());            
            System.out.println(p);            
            System.out.println(best.evaluate());
                        
            p.cullThis(0.25f);
            
            //System.out.println(p.size());
            
            Assert.assertTrue(p.size() < (individuals * 0.8));
            
            p = e.run();
            
            Assert.assertEquals(individuals, p.size());
            
            for (int i = 0; i < 3; i++) {
                p.cullThis(0.8f);
                p = e.run();
            }
            
            List<Individual> nextBest = Lists.newArrayList(p.elites(0.5f));
            
            System.out.println(firstBest);
            System.out.println(nextBest);
            
            //show some evolution in change of elites
        Assert.assertNotEquals(firstBest, nextBest);
            
	}
}