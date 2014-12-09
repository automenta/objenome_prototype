/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.approximation;

import objenome.op.Scalar;
import objenome.op.DiffableFunction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thorsten
 */
public class DiffableFunctionMarshaller {

    private final DiffableFunction f;
    private final Scalar[] inputs;
    private final Scalar[] parameters;
    private final double[] maxInputs;
    private final double[] minInputs;

    public DiffableFunctionMarshaller(DiffableFunctionGenerator diffableFunctionGenerator, int numInputs, int numFeatures) {
        inputs = new Scalar[numInputs];
        for (int i = 0; i < numInputs; ++i) {
            inputs[i] = new Scalar(0, "x" + i);
        }
        maxInputs = new double[inputs.length];
        minInputs = new double[inputs.length];
        List<Scalar> ps = new ArrayList<>();
        f = diffableFunctionGenerator.generate(inputs, ps, numFeatures);
        parameters = ps.toArray(new Scalar[ps.size()]);

    }

    public void setInputs(double[] inputs) {
        for (int i = 0; i < inputs.length; ++i) {
            double x = inputs[i];         
            double max = maxInputs[i];
            double min = minInputs[i];
            if (x > max) {
                max = maxInputs[i] = x;
            }
            if (x < min) {
                min = minInputs[i] = x;
            }
            if (max == min) {
                max = maxInputs[i] = minInputs[i] + 1;
            }
            this.inputs[i].setValue((x - min) / (max - min));
        }
    }

    public DiffableFunction getF() {
        return f;
    }

    public Scalar[] getParameters() {
        return parameters;
    }
}
