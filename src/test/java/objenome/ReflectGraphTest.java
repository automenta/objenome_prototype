/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import objenome.dependency.Builder;
import com.google.common.collect.SetMultimap;
import objenome.util.ReflectGraph;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author me
 */
public class ReflectGraphTest {
    
    @Test 
    public void test1() {

        
    
        SetMultimap<Class, Class> anc = new ReflectGraph(/*new String[] { "objenome" },*/
                Genetainer.class, Container.class, Builder.class)                
                .getAncestorImplementations();

        assertEquals(6, anc.keySet().size());
        assertTrue(anc.size() > anc.keySet().size());
        
//        for (Class c : anc.keySet()) {
//            System.out.println(c + "=" + anc.get(c));
//        }
            
    }
    
    
}
