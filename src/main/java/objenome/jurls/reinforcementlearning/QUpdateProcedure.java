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
public class QUpdateProcedure implements UpdateProcedure {

    ArrayRealVector gr = null;
    
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
        double vtm1 = Utils.v(fm, s[0], lower, upper, num)[1];
        double vt = Utils.v(fm, s[1], lower, upper, num)[1];
        gr = Utils.gradient(fm, s[0], a[0], gr);

        double[] deltas = Utils.add(
                Utils.mult(reward + rLParameters.getGamma() * vt - qtm1, gr.getDataRef()), 
                Utils.mult(reward + rLParameters.getGamma() * vt - vtm1, context.e)
        );
        
        deltas = Utils.add(
                Utils.mult(approxParameters.getAlpha(),Utils.normalize(deltas)),
                Utils.mult(approxParameters.getMomentum(),context.previousDeltas)
        );
                
        for (int i = 0; i < deltas.length; ++i) {
            fm.getParameters()[i].setValue(fm.getParameters()[i].value() + deltas[i]);
        }
        
        context.e = Utils.mult(
                rLParameters.getGamma() * rLParameters.getLambda(),
                Utils.sub(gr.getDataRef(), context.e)
        );
        
        context.previousDeltas = deltas;
    }
}
