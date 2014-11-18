/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.List;

/**
 * Gene of an Objosome
 */
abstract public class Objene extends AtomicDouble {
   
    /**
     * the DI target instance key that this affects
     */
    public final List<Object> path;

   
    public Objene(List<Object> path, double initialValue) {
        super(initialValue);
        this.path = path;
    }
    
    
}
