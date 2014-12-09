/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.reinforcementlearning;

import objenome.solution.approximation.ApproxParameters;
import objenome.solution.approximation.DiffableFunctionMarshaller;

/**
 *
 * @author thorsten
 */
public interface UpdateProcedure {

    public static class Context {

        public double[] e;
        public double[] previousDeltas;
    }

    public void update(
            ApproxParameters approxParameters,
            RLParameters rLParameters,
            Context context,
            double reward,
            double[][] s,
            double[] a,
            DiffableFunctionMarshaller fm,
            double lower,
            double upper,
            int num);
}
