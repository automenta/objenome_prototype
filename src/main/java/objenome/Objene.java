/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * Gene of an Objosome
 */
abstract public class Objene extends AtomicDouble {

    public Objene(double initialValue) {
        super(initialValue);
    }
    
    
}
