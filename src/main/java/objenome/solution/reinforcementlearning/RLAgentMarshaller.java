/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.reinforcementlearning;

import objenome.solution.approximation.ApproxParameters;
import objenome.solution.approximation.DiffableFunctionGenerator;
import objenome.solution.approximation.DiffableFunctionMarshaller;

/**
 *
 * @author thorsten
 */
public class RLAgentMarshaller {

    private RLAgent rLAgent;
    private DiffableFunctionMarshaller functionMarshaller;

    public void reset(
            DiffableFunctionGenerator diffableFunctionGenerator,
            int numFeatures,
            UpdateProcedure updateProcedure,
            ActionSelector actionSelector,
            double[] s0,
            ApproxParameters approxParameters,
            RLParameters rLParameters,
            int numActions,
            double minAction,
            double maxAction
    ) {
        functionMarshaller = new DiffableFunctionMarshaller(diffableFunctionGenerator, s0.length + 1, numFeatures);
        rLAgent = new RLAgent(functionMarshaller, updateProcedure, actionSelector, numActions, minAction, maxAction, s0, approxParameters, rLParameters);
    }

    public RLAgent getRLAgent() {
        return rLAgent;
    }

    public DiffableFunctionMarshaller getFunctionMarshaller() {
        return functionMarshaller;
    }
}
