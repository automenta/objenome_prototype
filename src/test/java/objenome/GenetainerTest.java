/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open of template in of editor.
 */
package objenome;

import static objenome.Builder.of;
import static objenome.Builder.the;
import objenome.gene.ClassSelect;
import objenome.gene.IntegerSelect;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author me
 */
public class GenetainerTest {

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
        Genetainer c = new Genetainer();
        c.any(Part.class, of(Part0.class, Part1.class));
                        
        Objenome o = c.genome(Machine.class);
        assertEquals("obgenome contains one gene to select betwen the implementations of interface Part", 1, o.size());
        
        assertEquals(ClassSelect.class, o.get(0).getClass());
        
        assertEquals("[ClassBuilder[class objenome.GenetainerTest$Machine], objenome.GenetainerTest$Part arg0 (part)]", o.get(0).path.toString());
    }
    
    
    @Test public void testSimpleObjeneGeneration1() {
        Genetainer c = new Genetainer();
        c.any(Part.class, of(Part0.class, Part1.class));
                        
        Objenome o = c.genome(Part.class);
        
        assertEquals(1, o.size());        
        assertEquals(ClassSelect.class, o.get(0).getClass());        
    }
    
    //18553247676
    @Test public void testSimpleObjeneGeneration11() {
        System.out.println("-------------");
        Genetainer c = new Genetainer();
        c.any(Part.class, the(PartN.class));
                        
        Objenome o = c.genome(Part.class);
        
        assertEquals(1, o.size());        
        assertEquals(IntegerSelect.class, o.get(0).getClass());
                
        System.out.println("-------------");
    }
    
    
    /** one gene to select between two interfaces with parametric constructor in one of of dependencies */
    @Test public void testSimpleObjeneGeneration2() {
        
        Genetainer c = new Genetainer();
        c.any(Part.class, of(Part0.class, Part1.class, PartN.class));
        Objenome o = c.genome(Machine.class);
        
        assertEquals("obgenome contains 2 genes: a) to select betwen the implementations of interface Part, and b) to set the int parameter for PartN if that needs instantiated", 2, o.size());
        assertEquals(ClassSelect.class, o.get(0).getClass());
                 
        assertEquals("[ClassBuilder[class objenome.GenetainerTest$Machine], objenome.GenetainerTest$Part arg0 (part)]", o.get(0).path.toString());
        
        assertEquals(IntegerSelect.class, o.get(1).getClass());        
                        
        assertEquals("[ClassBuilder[class objenome.GenetainerTest$Machine], objenome.GenetainerTest$Part arg0 (part), ClassBuilder[class objenome.GenetainerTest$PartN], int arg0]", o.get(1).path.toString());
    }
    
    @Test public void testRecurse2LevelsGeneration() {
        
        Genetainer c = new Genetainer();
        c.usable(Part.class, PartWithSubComponent.class);
        c.any(SubComponent.class, of(SubComponent0.class, SubComponent1.class));
        Objenome o = c.genome(Machine.class);
        
        assertEquals("obgenome contains 1 gene: to select between subcomponents of the part component", 1, o.size());
        assertEquals(ClassSelect.class, o.get(0).getClass());
        assertEquals("3rd level deep", 5, o.get(0).path.size());
    }
    
    @Test public void testMultitypeRecurse() {
        
        Genetainer c = new Genetainer();
        c.any(Part.class, 
                    of(Part0.class, Part1.class, PartN.class, PartWithSubComponent.class));
        c.any(SubComponent.class, 
                    of(SubComponent0.class, SubComponent1.class));
        Objenome o = c.genome(Machine.class);
                       
        assertEquals(3, o.size());
        assertEquals(ClassSelect.class, o.get(0).getClass());
        assertEquals(IntegerSelect.class, o.get(1).getClass());
        assertEquals(1, ((IntegerSelect)o.get(1)).getMin());
        assertEquals(3, ((IntegerSelect)o.get(1)).getMax());
        assertEquals(ClassSelect.class, o.get(2).getClass());
    }
    

    


}
