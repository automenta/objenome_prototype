/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import org.junit.Test;

/**
 *
 * @author me
 */
public class TestSimple {

    public interface Part {
        public int function();        
    }
    
    public class Part0 implements Part {
        @Override public int function() { return 0;  }
    }
    public class Part1 implements Part {
        @Override public int function() { return 1; }
    }
    public class PartN implements Part {
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
        c.usable(Part.class, Part0.class);
        c.usable(Part.class, Part1.class);
        c.usable(Part.class, PartN.class);
        
        Objosome o = c.newObjosome();
        
    }
}
