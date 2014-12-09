/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.reinforcementlearning;

/**
 *
 * @author thorsten
 */
public class EpsilonGreedyActionSelector implements ActionSelector {

    public static class Parameters {

        private double epsilon;

        public Parameters(double epsilon) {
            this.epsilon = epsilon;
        }

        public double getEpsilon() {
            return epsilon;
        }

        public void setEpsilon(double epsilon) {
            this.epsilon = epsilon;
        }
    }

    private final Parameters parameters;

    public EpsilonGreedyActionSelector(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public double[][] fromQValuesToProbabilities(double[][] actionValuePairs) {
        int bestPair = 0;
        
        for(int i = 0;i < actionValuePairs.length;++i){
            if(actionValuePairs[i][1] > actionValuePairs[bestPair][1])
                bestPair = i;
        }
        
        double[][] ret = new double[actionValuePairs.length][2];
        
        for(int i = 0;i < actionValuePairs.length;++i){
            ret[i][0] = actionValuePairs[i][0];
            if(i == bestPair){
                ret[i][1] = 1 - parameters.epsilon;
            }else{
                ret[i][1] = parameters.epsilon / (ret.length - 1);
            }
        }
        
        return ret;
    }

}
