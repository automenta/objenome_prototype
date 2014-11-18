/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.util.concurrent.AtomicDouble;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Gene of an Objosome
 */
abstract public class Objene<V> extends AtomicDouble {
   
    /**
     * the DI target instance key that this affects.
     * will consist of alternating items between:
     *  --Class (input target class)
     *  --ClassBuilder (solved dependent Class)
     *  --DependencyKey (which will contain a String key and possible Parameter reference)
     * 
     */
    public final List<Object> path;

   
    public Objene(List<Object> path, double initialValue) {
        super(initialValue);
        this.path = path;
    }


    abstract public V getValue();
    
    @Override
    public String toString() {
        Object lastPathElement = path.get(path.size()-1);        
        return lastPathElement + "=" + getValue();
    }    
    
}
