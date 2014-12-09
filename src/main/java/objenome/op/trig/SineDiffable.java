/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.op.trig;

import objenome.op.DiffableFunction;
import objenome.op.Scalar;
import objenome.op.Numeric1dDiff;

/**
 *
 * @author me
 */
public class SineDiffable extends Sine<Numeric1dDiff> implements DiffableFunction {

    @Override
    public double partialDerive(Scalar parameter) {
        return input().partialDerive(parameter) * Math.cos(input().value());
    }
    
}
