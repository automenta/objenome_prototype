/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.jurls;

import objenome.op.Scalar;
import objenome.op.DiffableFunction;

/**
 *
 * @author thorsten
 */
public class Exp implements DiffableFunction{
    private final DiffableFunction x;

    public Exp(DiffableFunction x) {
        this.x = x;
    }
    
    @Override
    public double value() {
        return Math.exp(x.value());
    }

    @Override
    public double partialDerive(Scalar parameter) {
        return x.partialDerive(parameter) * Math.exp(x.value());
    }
    
}
