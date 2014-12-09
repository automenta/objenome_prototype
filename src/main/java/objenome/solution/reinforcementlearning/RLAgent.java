/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.reinforcementlearning;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import objenome.solution.approximation.ApproxParameters;
import objenome.solution.approximation.DiffableFunctionMarshaller;
import objenome.util.Utils;

/**
 *
 * @author thorsten
 */
public class RLAgent {

    private final DiffableFunctionMarshaller functionMarshaller;
    private final UpdateProcedure updateProcedure;
    private final double[][] s = new double[2][];
    private final double[] a = new double[2];
    private final int numActions;
    private final double lowerAction;
    private final double upperAction;
    private final Random random = new Random();
    private final RLParameters rLParameters;
    private final ApproxParameters approxParameters;
    private final UpdateProcedure.Context context = new UpdateProcedure.Context();
    private final ActionSelector actionSelector;
    public int numIterations = 0;

    public RLAgent(
            DiffableFunctionMarshaller functionMarshaller,
            UpdateProcedure updateProcedure,
            ActionSelector actionSelector,
            int numActions,
            double lowerAction,
            double upperAction,
            double[] s0,
            ApproxParameters approxParameters,
            RLParameters rLParameters
    ) {
        this.functionMarshaller = functionMarshaller;
        this.updateProcedure = updateProcedure;
        this.actionSelector = actionSelector;
        this.numActions = numActions;
        this.lowerAction = lowerAction;
        this.upperAction = upperAction;
        context.previousDeltas = new double[functionMarshaller.getParameters().length];
        context.e = new double[functionMarshaller.getParameters().length];
        this.approxParameters = approxParameters;
        this.rLParameters = rLParameters;
        push(s0);
    }

    private void push(double[] _s) {
        s[0] = s[1];
        s[1] = _s;
    }

    private void push(double _a) {
        a[0] = a[1];
        a[1] = _a;
    }

    public void learn(double[] state, double reward) {
        push(state);
        push(chooseAction(state));
        updateProcedure.update(approxParameters, rLParameters, context, reward, s, a, functionMarshaller, lowerAction, upperAction, numActions);
        numIterations++;
    }

    public double chooseAction() {
        return a[1];
    }

    public double[][] getActionProbabilities(double[] state) {
        double[][] actionValuePairs = new double[numActions][2];

        for (int i = 0; i < numActions; ++i) {
            double a = i * (upperAction - lowerAction) / (numActions - 1) + lowerAction;
            actionValuePairs[i][0] = a;
            actionValuePairs[i][1] = Utils.q(functionMarshaller, state, a);
        }

        return actionSelector.fromQValuesToProbabilities(actionValuePairs);
    }

    public double chooseAction(double[] state) {
        double[][] actionProbabilityPairs = getActionProbabilities(state);
        Arrays.sort(actionProbabilityPairs, (double[] o1, double[] o2) -> (int) Math.signum(o1[1] - o2[1]));

        double x = Math.random();
        int i = -1;

        while (x > 0) {
            ++i;
            x -= actionProbabilityPairs[i][1];
        }

        return actionProbabilityPairs[i][0];
    }
}
