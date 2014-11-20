/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.gene;

/**
 * Indicates the gene produces a Number result (Ex: Integer, Double)
 */
public interface Numeric {
    
    public Number getMin();
    public Number getMax();
    
    public Number getNumber();

    public void setValue(double d);
    
}
