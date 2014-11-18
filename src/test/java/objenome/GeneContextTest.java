/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import objenome.gene.ClassSelect;
import objenome.gene.IntegerSelect;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author me
 */
public class GeneContextTest {

    public static interface Part { public int function();    }
    public static interface SubComponent { public int function();    }

    public static class SubComponent0 implements SubComponent {
        @Override public int function() { return 0; }
    }
    public static class SubComponent1 implements SubComponent {
        @Override public int function() {  return 1;}         
    }
    
    public static class Part0 implements Part {
        @Override public int function() { return 0;  }
    }
    public static class Part1 implements Part {
        @Override public int function() { return 1; }
    }
    public static class PartN implements Part {
        private final int value;

        public PartN( @Between(min=1, max=3) int arg0) {
            this.value = arg0;
        }
        
        @Override public int function() { return value; }
    }
    public static class PartWithSubComponent implements Part {
        private final SubComponent subcomp;
        public PartWithSubComponent(SubComponent subcomponent) {         
            this.subcomp = subcomponent;                    
        }        
        @Override public int function() { return subcomp.function();  }
    }

    public static class Machine {
        public final Part part;
        
        public Machine(Part p) {
            this.part = p;
        }
        public int function() {
            return part.function();
        }
    }
    
//    public static class ParametricMachine {
//        public final Part part;
//        
//        public Machine(Part p, double value) {
//            this.part = p;
//        }
//        public int function() {
//            return part.function();
//        }
//    }
    
    

    public static class MachineWithParametricPart {
        public final PartWithSubComponent part;
        
        public MachineWithParametricPart(PartWithSubComponent p) {
            this.part = p;
        }
        public int function() {
            return part.function();
        }
    }
    
    
    /** one gene to select between two interfaces with non-parametric constructors */
    @Test public void testSimpleObjeneGeneration() {
        GeneContext c = new GeneContext();
        c.usable(Part.class, Part0.class, Part1.class);
                        
        Objosome o = c.get(Machine.class);
        assertEquals("objosome contains one gene to select betwen the implementations of interface Part", 1, o.getLength());
        
        assertEquals(ClassSelect.class, o.get(0).getClass());
        
        assertEquals("[class objenome.GeneContextTest$Machine, objenome.GeneContextTest$Part arg0 (part)]", o.get(0).path.toString());
    }
    
    /** one gene to select between two interfaces with parametric constructor in one of the dependencies */
    @Test public void testSimpleObjeneGeneration2() {
        
        GeneContext c = new GeneContext();
        c.usable(Part.class, Part0.class, Part1.class, PartN.class);                        
        Objosome o = c.get(Machine.class);
        
        assertEquals("objosome contains 2 genes: a) to select betwen the implementations of interface Part, and b) to set the int parameter for PartN if that needs instantiated", 2, o.getLength());
        assertEquals(ClassSelect.class, o.get(0).getClass());
                 
        assertEquals("[class objenome.GeneContextTest$Machine, objenome.GeneContextTest$Part arg0 (part)]", o.get(0).path.toString());
        
        assertEquals(IntegerSelect.class, o.get(1).getClass());        
                        
        assertEquals("[class objenome.GeneContextTest$Machine, objenome.GeneContextTest$Part arg0 (part), class objenome.GeneContextTest$PartN, int arg0]", o.get(1).path.toString());
    }
    
    @Test public void testRecurse2LevelsGeneration() {
        
        GeneContext c = new GeneContext();
        c.usable(Part.class, PartWithSubComponent.class);
        c.usable(SubComponent.class, SubComponent0.class, SubComponent1.class);
        Objosome o = c.get(Machine.class);
        
        assertEquals("objosome contains 1 gene: to select between subcomponents of the part component", 1, o.getLength());
        assertEquals(ClassSelect.class, o.get(0).getClass());
        assertEquals("3rd level deep", 5, o.get(0).path.size());
    }
    
    @Test public void testMultitypeRecurse() {
        
        GeneContext c = new GeneContext();
        c.usable(Part.class, 
                    Part0.class, Part1.class, PartN.class, PartWithSubComponent.class);
        c.usable(SubComponent.class, 
                    SubComponent0.class, SubComponent1.class);
        Objosome o = c.get(Machine.class);
                       
        assertEquals(3, o.getLength());
        assertEquals(ClassSelect.class, o.get(0).getClass());
        assertEquals(IntegerSelect.class, o.get(1).getClass());
        assertEquals(1, ((IntegerSelect)o.get(1)).getMin());
        assertEquals(3, ((IntegerSelect)o.get(1)).getMax());
        assertEquals(ClassSelect.class, o.get(2).getClass());
    }
    

    @Test public void testAmbiguityDenial() {
        
        DefaultContext c = new DefaultContext();
        c.usable(Part.class, Part0.class);
        assertEquals(0, c.get(Machine.class).function());
        c.usable(Part.class, Part1.class);
        assertEquals("overrides the first builder", 1, c.get(Machine.class).function());
        
    }

}