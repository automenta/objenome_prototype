/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open of template in of editor.
 */
package objenome;

import static objenome.Builder.of;
import static objenome.Builder.of;
import objenome.GenetainerTest.Machine;
import objenome.GenetainerTest.Part;
import objenome.GenetainerTest.Part0;
import objenome.GenetainerTest.PartN;
import objenome.gene.ClassSelect;
import objenome.gene.IntegerSelect;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author me
 */
public class PhenotainerTest {

    
    @Test public void testIntegerParameterObjosomeContext() {
        Genetainer g = new Genetainer();

        g.any(Part.class, of(PartN.class));
                        
        Objenome o = g.genome(Part.class);

        IntegerSelect partSelect = (IntegerSelect)o.get(0);
        
        Container c = o.container();
        
        Part m = c.get(Part.class);
        
        int expectedResult = partSelect.getValue();
        
        assertEquals(expectedResult, m.function());        
        
    }
    
    
    /*@Test*/ public void testSimpleObjosomeContext() {
        Genetainer g = new Genetainer();

        g.any(GenetainerTest.Part.class, of(GenetainerTest.Part0.class, GenetainerTest.Part1.class));
                        
        Objenome o = g.genome(GenetainerTest.Machine.class);

        ClassSelect partSelect = (ClassSelect)o.get(0);
        
        Container c = o.container();
        
        Machine m = c.get(Machine.class);
        
        int expectedResult = partSelect.getValue() == Part0.class ? 0 : 1;
        
        assertEquals(expectedResult, m.function());        
        
    }

    
}
