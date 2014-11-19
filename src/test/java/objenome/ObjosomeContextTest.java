/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import objenome.GeneContextTest.Machine;
import objenome.GeneContextTest.Part0;
import objenome.gene.ClassSelect;
import objenome.gene.IntegerSelect;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author me
 */
public class ObjosomeContextTest {

    
    @Test public void testIntegerParameterObjosomeContext() {
        Genetainer g = new Genetainer();

        g.use(GeneContextTest.Part.class, GeneContextTest.PartN.class);
                        
        Objosome o = g.get(GeneContextTest.Machine.class);

        IntegerSelect partSelect = (IntegerSelect)o.get(0);
        
        Container c = o.context();
        
        Machine m = c.get(Machine.class);
        
        int expectedResult = partSelect.getValue();
        
        assertEquals(expectedResult, m.function());        
        
    }
    
    
    @Test public void testSimpleObjosomeContext() {
        Genetainer g = new Genetainer();

        g.usable(GeneContextTest.Part.class, GeneContextTest.Part0.class, GeneContextTest.Part1.class);
                        
        Objosome o = g.get(GeneContextTest.Machine.class);

        ClassSelect partSelect = (ClassSelect)o.get(0);
        
        Container c = o.context();
        
        Machine m = c.get(Machine.class);
        
        int expectedResult = partSelect.getValue() == Part0.class ? 0 : 1;
        
        assertEquals(expectedResult, m.function());        
        
    }

    
}
