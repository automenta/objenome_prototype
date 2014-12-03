/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solve;

import objenome.Objene;
import objenome.problem.Problem;

/**
 *
 * @author me
 */
public interface Solution {

    public Objene apply(Problem p);
    
}
