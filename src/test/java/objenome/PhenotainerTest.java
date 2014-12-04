/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open of template in of editor.
 */
package objenome;

import java.util.HashSet;
import java.util.Set;
import objenome.GenetainerTest.Machine;
import objenome.GenetainerTest.Part;
import objenome.GenetainerTest.Part0;
import objenome.GenetainerTest.Part1;
import objenome.GenetainerTest.PartN;
import objenome.GenetainerTest.PartWithSubPart;
import objenome.GenetainerTest.SubPart0;
import objenome.GenetainerTest.SubPart1;
import objenome.solution.SetImplementationClass;
import objenome.solution.SetIntegerValue;
import objenome.solution.dependency.Builder;
import static objenome.solution.dependency.Builder.of;
import objenome.solution.dependency.ClassBuilder;
import objenome.solution.dependency.DecideImplementationClass;
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
        assertTrue("an any expression of one element is reduced to an autowired 'use()'", (builder instanceof ClassBuilder) && (!(builder instanceof DecideImplementationClass)));
                        
        Objenome o = g.random(Part.class);

        SetIntegerValue partSelect = (SetIntegerValue)o.getSolutions().get(0);
        
        Part m = o.get(Part.class);
        assertNotNull(m);
        
        int expectedResult = partSelect.getValue();
        
        assertEquals(expectedResult, m.function());        
        
    }
    
    
    @Test public void testSimpleObjosome() {
        Genetainer g = new Genetainer();

        g.any(Part.class, of(Part0.class, Part1.class));
                        
        Objenome o = g.random(Machine.class);

        assertEquals(1, o.getSolutionSize());
        
        SetImplementationClass partSelect = (SetImplementationClass)o.getSolutions().get(0);
        
        Machine m = o.get(Machine.class);
        
        int expectedResult = partSelect.getValue() == Part0.class ? 0 : 1;
        
        assertEquals(expectedResult, m.function());        
        
        Machine possiblyDifferent = o.mutate().get(Machine.class);
        assertTrue(possiblyDifferent!=null);
        
        
    }

    @Test public void testMultiGene() {
        Genetainer g = new Genetainer();

        g.any(Part.class, of(Part0.class, Part1.class, PartN.class));
                        
        Objenome o = g.random(Machine.class);
        
        assertTrue(o.getSolutionSize() > 1);                

        
                
        Machine m = o.get(Machine.class);
        assertNotNull(m);
        

        
        Machine n = o.get(Machine.class);                
        assertNotNull(n);
        assertTrue(n.function() > -1);
        
    }

    @Test public void testMultiGene2() {
        
        Genetainer g = new Genetainer();

        g.any(PartWithSubPart.class, of(SubPart0.class, SubPart1.class));
        g.any(Part.class, of(Part0.class, Part1.class, PartN.class));

        //find Part dependency of Machine recursively without being specified
        Objenome o = g.random(Machine.class/*, Part.class*/);        
        
        System.out.println(o.getSolutions());
        
        assertEquals("one solution for subpart impl choice, one for part impl choice, and 3rd for PartN parameter", 3, o.getSolutionSize());

        Container c = o.container();
        
        for (Builder b : c.getBuilders().values()) {
            assertTrue(!(b instanceof DecideImplementationClass));                
        }
        
        Machine m = o.get(Machine.class);
                
        assertTrue(m.function() > -1);
    }
    
    @Test public void testReuse() {
        Genetainer g = new Genetainer();

        g.any(Part.class, of(Part0.class, Part1.class, PartN.class));
                        
        Objenome o = g.random(Machine.class);

        Set<Class> uniqueClasses = new HashSet();
        for (int i = 0; i < 55; i++) {  
            
            Container c = o.container();
            Machine m = c.get(Machine.class);
            
            
            assertEquals("iteration " + i,  ((SetImplementationClass)o.getSolutions().get(0)).getValue(), m.part.getClass() );
            uniqueClasses.add(m.part.getClass());
            
            o.mutate();            
        }
        
        assertEquals(3, uniqueClasses.size());
        
        
        
    }    
}
