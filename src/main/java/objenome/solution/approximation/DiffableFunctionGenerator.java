/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.approximation;

import objenome.op.Scalar;
import objenome.op.DiffableFunction;
import java.util.List;

/**
 *
 * @author thorsten
 */
public interface DiffableFunctionGenerator {

    public DiffableFunction generate(
            Scalar[] inputs,
            List<Scalar> parameterList,
            int numFeatures
    );
}
