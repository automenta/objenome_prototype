/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.jurls.approximation;

/**
 *
 * @author thorsten
 */
public interface ParameterizedFunction {

    public double compute(double[] xs);

    public void oneStepTowards(double[] xs, double y);

    public int getNumberOfParameters();
}
