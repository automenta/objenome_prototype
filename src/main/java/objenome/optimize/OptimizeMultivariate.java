/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.optimize;

import java.util.function.Function;
import objenome.Objenome;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import static org.apache.commons.math3.optim.nonlinear.scalar.GoalType.MAXIMIZE;
import org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.MultiDirectionalSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;

/**
 * Multivariate Optmization with Multistart which uses different starting points 
 * (trying to avoid being trapped in a local extremum when looking for a global one)
 * @see http://commons.apache.org/proper/commons-math/javadocs/api-3.3/src-html/org/apache/commons/math3/optim/nonlinear/scalar/MultiStartMultivariateOptimizer.html
 */
public class OptimizeMultivariate<C> extends NumericSolver<C> implements MultivariateFunction {

    int numStarts = -1;
    double convergeRel = 0.01;
    double convergeAbs = 0.02;
    private Double bestValue;
    int evaluations = 200;
    GoalType goal = MAXIMIZE;
    
    public OptimizeMultivariate(Objenome o, Class<? extends C> model, Function<C, Double> function) {
        super(o, model, function);
    }

    @Override
    public void run() {
        if (numStarts==-1)
            numStarts = variables.size() * 2;
        
        MultiStartMultivariateOptimizer m = new MultiStartMultivariateOptimizer(getOptimizer(), numStarts, getRandomVectorizer());
        PointValuePair result 
            = m.optimize(new MaxEval(200), goal, new ObjectiveFunction(this),
                    new MultiDirectionalSimplex(variables.size()));
                    //new InitialGuess(new double[] { 0 } ));
        
        apply(result.getPointRef());
        this.bestValue = result.getValue();
    }

    /** the resulting scalar evaluation of the sought maxima/minima */
    public Double getBestValue() {
        return bestValue;
    }
       
    
    protected void apply(double[] values) {
        for (int i = 0; i < values.length; i++)
            variables.get(i).setValue(values[i]);
        objenome.commit();
    }

    @Override
    public double value(double[] v) {
        apply(v);
        return function.apply( objenome.get(model) );
    }

    
    public MultivariateOptimizer getOptimizer() {
        return new SimplexOptimizer(convergeRel, convergeAbs);
    }
    

    private RandomVectorGenerator getRandomVectorizer() {
        return new UnitSphereRandomVectorGenerator(variables.size());
    }

    
    
}
