/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open of template in of editor.
 */
package objenome;

import static objenome.Builder.of;
import objenome.GenetainerTest.Machine;
import objenome.GenetainerTest.Part;
import objenome.GenetainerTest.Part0;
import objenome.GenetainerTest.Part1;
import objenome.GenetainerTest.PartN;
import objenome.GenetainerTest.PartWithSubPart;
import objenome.GenetainerTest.SubPart0;
import objenome.GenetainerTest.SubPart1;
import objenome.gene.ClassSelect;
import objenome.gene.IntegerSelect;
import objenome.impl.ClassBuilder;
import objenome.impl.MultiClassBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author me
 */
public class PhenotainerTest {

    
    @Test public void testIntegerParameterObjosome() {
        Genetainer g = new Genetainer();

        Builder builder = g.any(Part.class, of(PartN.class));
        assertTrue("an any expression of one element is reduced to an autowired 'use()'", (builder instanceof ClassBuilder) && (!(builder instanceof MultiClassBuilder)));
                        
        Objenome o = g.genome(Part.class);

        IntegerSelect partSelect = (IntegerSelect)o.getGeneList().get(0);
        
        Container c = o.container();
        
        Part m = c.get(Part.class);
        assertNotNull(m);
        
        int expectedResult = partSelect.getValue();
        
        assertEquals(expectedResult, m.function());        
        
    }
    
    
    @Test public void testSimpleObjosome() {
        Genetainer g = new Genetainer();

        g.any(Part.class, of(Part0.class, Part1.class));
                        
        Objenome o = g.genome(Machine.class);

        ClassSelect partSelect = (ClassSelect)o.getGeneList().get(0);
        
        Container c = o.container();
        
        Machine m = c.get(Machine.class);
        
        int expectedResult = partSelect.getValue() == Part0.class ? 0 : 1;
        
        assertEquals(expectedResult, m.function());        
        
    }

    @Test public void testMultiGene() {
        Genetainer g = new Genetainer();

        g.any(Part.class, of(Part0.class, Part1.class, PartN.class));
                        
        Objenome o = g.genome(Machine.class);
        
        assertTrue(o.size() > 1);                

        
        
        Container c = o.container();        
        Machine m = c.get(Machine.class);
        assertNotNull(m);
        

        
        Machine n = o.get(Machine.class);                
        assertNotNull(n);
        assertTrue(n.function() > -1);
        
    }

    @Test public void testMultiGene2() {
        System.out.println("-----/");
        
        Genetainer g = new Genetainer();

        g.any(PartWithSubPart.class, of(SubPart0.class, SubPart1.class));
        g.any(Part.class, of(Part0.class, Part1.class, PartN.class)); //, PartWithSubPart.class));        
                        
        Objenome o = g.genome(Machine.class, Part.class/*, PartWithSubPart.class*/);        
        System.out.println(" ==");
        for (Objene x : o.getGeneList()) {            
            System.out.println(x.key() + ":  "+ x);
        }
        
        
        assertTrue(o.size() > 1);
        
        System.out.println("parent builders:");
        System.out.println(g.getBuilders());
        
        Container c = o.container();
        
        for (Builder b : c.getBuilders().values()) {
            assertTrue(!(b instanceof MultiClassBuilder));                
        }
        
        System.out.println("child builders:");
        System.out.println(c.getBuilders());
        
        Machine m = c.get(Machine.class);
                
        assertTrue(m.function() > -1);
        System.out.println("/-----");
    }
    
}
