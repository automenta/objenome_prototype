/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.jurls.reinforcementlearning;

/**
 *
 * @author thorsten
 */
public class ByQActionSelector implements ActionSelector {

    @Override
    public double[][] fromQValuesToProbabilities(double[][] actionValuePairs) {
        double[][] ret = new double[actionValuePairs.length][2];

        for (int i = 0; i < actionValuePairs.length; ++i) {
            ret[i][0] = actionValuePairs[i][0];
            ret[i][1] = actionValuePairs[i][1];
        }

        double min = Double.MAX_VALUE;
        for (int i = 0; i < ret.length; ++i) {
            if (ret[i][1] < min) {
                min = ret[i][1];
            }
        }

        for (int i = 0; i < ret.length; ++i) {
            ret[i][1] += min;
        }

        double sum = 0;
        for (int i = 0; i < ret.length; ++i) {
            sum += ret[i][1];
        }

        for (int i = 0; i < ret.length; ++i) {
            ret[i][1] /= sum;
        }

        for (int i = 0; i < ret.length; ++i) {
            ret[i][1] = Math.pow(ret[i][1],2);
        }

        sum = 0;
        for (int i = 0; i < ret.length; ++i) {
            sum += ret[i][1];
        }

        for (int i = 0; i < ret.length; ++i) {
            ret[i][1] /= sum;
        }
        return ret;
    }

}
