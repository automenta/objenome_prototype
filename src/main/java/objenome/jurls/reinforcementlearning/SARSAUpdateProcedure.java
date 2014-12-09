/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.jurls.reinforcementlearning;

import objenome.jurls.approximation.ApproxParameters;
import objenome.jurls.approximation.DiffableFunctionMarshaller;
import objenome.util.Utils;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author thorsten
 */
public class SARSAUpdateProcedure implements UpdateProcedure {
    private ArrayRealVector gr;

    @Override
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
            int num
    ) {
        double qtm1 = Utils.q(fm, s[0], a[0]);
        double qt = Utils.q(fm, s[1], a[1]);
        gr = Utils.gradient(fm, s[1], a[1], gr);

        double[] deltas = Utils.mult(
                reward + rLParameters.getGamma() * qt - qtm1,
                context.e
        );

        deltas = Utils.add(
                Utils.mult(approxParameters.getAlpha(), Utils.normalize(deltas)),
                Utils.mult(approxParameters.getMomentum(), context.previousDeltas)
        );

        for (int i = 0; i < deltas.length; ++i) {
            fm.getParameters()[i].setValue(fm.getParameters()[i].value() + deltas[i]);
        }

        context.e = Utils.add(
                Utils.mult(
                        rLParameters.getGamma() * rLParameters.getLambda(),
                        context.e
                ),
                gr.getDataRef()
        );

        context.previousDeltas = deltas;
    }
}
