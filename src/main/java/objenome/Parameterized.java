/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import java.util.Collection;
import java.util.List;

/**
 * 
 */
public interface Parameterized {

    /** the genes necessary to specify an instance of this component */
    public Collection<? extends Objene> getGenes(List<Object> path);
    
}
