/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author me
 */
public class TestSimple {

    public interface Part {
        public int function();        
    }
    
    public static class Part0 implements Part {
        public Part0() {         }        
        @Override public int function() { return 0;  }
    }
    public static class Part1 implements Part {
        public Part1() {         }
        @Override public int function() { return 1; }
    }
    public static class PartN implements Part {
        private final int value;

        public PartN(int value) {
            this.value = value;
        }
        
        @Override public int function() { return value; }
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
    
    
    @Test public void testIt() {
        GeneContext c = new GeneContext();
        c.usable(Part.class, Part0.class, Part1.class, PartN.class);
                        
        Objosome o = c.newObjosome(Machine.class);
        assertEquals("objosome contains one gene to select betwen the implementations of interface Part", 1, o.getLength());
    }

    @Test public void testAmbiguityDenial() {
        
        DefaultContext c = new DefaultContext();
        c.usable(Part.class, Part0.class);
        assertEquals(0, c.get(Machine.class).function());
        c.usable(Part.class, Part1.class);
        assertEquals("overrides the first builder", 1, c.get(Machine.class).function());
        
    }

}
