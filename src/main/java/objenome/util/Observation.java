/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.util;

/**
 *
 * @author me
 */
public class Observation<A,B> {
    
    final public double weight;
    final public A input;
    final public B output;

    public Observation(A input, B output) {
        this(input, output, 1.0);
    }
    
    public Observation(A input, B output, double weight) {
        this.input = input;
        this.output = output;
        this.weight = weight;
    }
    
    
}