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
public interface ActionSelector {
    public double[][] fromQValuesToProbabilities(double[][] actionValuePairs);
}