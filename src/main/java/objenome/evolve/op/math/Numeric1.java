/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.evolve.op.math;

import java.util.function.Function;
import objenome.evolve.op.Node;
import objenome.evolve.tools.NumericUtils;
import objenome.evolve.tools.TypeUtil;

/**
 * 1-input numeric function.
 * Takes all numeric inputs
 * Produces numeric output of the same type as input
 */
abstract public class Numeric1 extends Node implements Function<Double,Double> {
    
    Numeric1() {
        super(null);
    }
    
    Numeric1(Node child) {
        super(child);
    }
    
    @Override
    public String getIdentifier() {
        return getClass().getSimpleName().toUpperCase();
    }
    
    abstract public double func(double x);
    
    /**
     * Evaluates this function. The child node is evaluated, the result of which
     * must be a numeric type (one of Double, Float, Long, Integer). The result
     * is raised to the power of 2 and returned as the same type as the input.
     *
     * @return the result of evaluating the child squared
     */
    @Override
    public Object evaluate() {
        Object c = getChild(0).evaluate();

        double result = apply( NumericUtils.asDouble(c) );

        if (c instanceof Long) {
            return (long) result;
        } else if (c instanceof Float) {
            return (float) result;
        } else if (c instanceof Integer) {
            return (int) result;
        } else if (c instanceof Double) {
            return result;
        } else {
            return null;
        }
    }
    
    /**
     * Returns this function node's return type for the given child input types.
     * If there is one input type of a numeric type then the return type will be
     * that numeric type. In all other cases this method will return
     * <code>null</code> to indicate that the inputs are invalid.
     *
     * @return a numeric class or null if the input type is invalid.
     */
    @Override
    public Class<?> dataType(Class<?>... inputTypes) {
        if ((inputTypes.length == 1) && TypeUtil.isNumericType(inputTypes[0])) {
            return inputTypes[0];
        } else {
            return null;
        }
    }
    
    @Override public Double apply(Double x) {
        return func(x.doubleValue());
    }
}
