/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.approximation;

import objenome.op.Scalar;
import objenome.util.Utils;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author thorsten
 */
public class GradientFitter implements ParameterizedFunction {

    private final DiffableFunctionMarshaller functionMarshaller;
    private final double[] previousDeltas;
    private final ApproxParameters approxParameters;
    private ArrayRealVector gradient;

    public GradientFitter(ApproxParameters approxParameters,
            DiffableFunctionMarshaller functionMarshaller) {
        this.approxParameters = approxParameters;
        this.functionMarshaller = functionMarshaller;
        previousDeltas = new double[functionMarshaller.getParameters().length];
    }

    @Override
    public void oneStepTowards(double[] xs, double y) {
        functionMarshaller.setInputs(xs);

        double q = functionMarshaller.getF().value();
        double e = y - q;

        gradient = Utils.gradient(functionMarshaller.getF(), functionMarshaller.getParameters(), gradient);
        gradient.mapMultiplyToSelf(e);

        double l = Utils.length(gradient.getDataRef());
        if (l == 0) {
            l = 1;
        }

        double[] gr = gradient.getDataRef();
        for (int i = 0; i < gr.length; ++i) {
            gr[i] = approxParameters.getAlpha() * gr[i] / l
                    + approxParameters.getMomentum() * previousDeltas[i];
            previousDeltas[i] = gr[i];
        }

        for (int i = 0; i < gr.length; ++i) {
            Scalar fmi = functionMarshaller.getParameters()[i];
            fmi.setValue(fmi.value() + gr[i] );
        }
    }

    @Override
    public double compute(double[] xs) {
        functionMarshaller.setInputs(xs);
        return functionMarshaller.getF().value();
    }

    @Override
    public int getNumberOfParameters() {
        return functionMarshaller.getParameters().length;
    }
}
