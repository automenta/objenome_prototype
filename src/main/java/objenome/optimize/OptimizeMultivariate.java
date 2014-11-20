/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.optimize;

import java.util.function.Function;
import objenome.Genetainer;
import objenome.Objenome;
import objenome.gene.Numeric;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import static org.apache.commons.math3.optim.nonlinear.scalar.GoalType.MAXIMIZE;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

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
    
    public OptimizeMultivariate(Class<? extends C> model, Function<C, Double> function) {
        this(new Genetainer().genome(model), model, function);
    }
    
    public OptimizeMultivariate(Objenome o, Class<? extends C> model, Function<C, Double> function) {
        super(o, model, function);
    }

    
    public Objenome run() {
        if (numStarts==-1)
            numStarts = variables.size() * 2;
        
        double[] lower = new double[variables.size()];
        double[] upper = new double[variables.size()];
        double[] mid = new double[variables.size()];
        int j = 0;
        for (Numeric n : variables) {
            lower[j] = n.getMin().doubleValue();
            upper[j] = n.getMax().doubleValue();
            mid[j] = (lower[j] + upper[j]) * 0.5f;
            j++;
        }
        
        //TODO add MultiStart
        
        MultivariateOptimizer optimize = 
                new BOBYQAOptimizer(4);
                //new PowellOptimizer(0.01, 0.05);

        PointValuePair result = optimize.optimize(
                new MaxEval(200),
                new SimpleBounds(lower, upper),
                GoalType.MAXIMIZE,
                new InitialGuess(mid),
                new ObjectiveFunction(this)
                );        
        
        apply(result.getPointRef());
                
        this.bestValue = result.getValue();
        return objenome;
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

    
//    public MultivariateOptimizer getOptimizer() {
//        return new SimplexOptimizer(convergeRel, convergeAbs);
//    }
    

//    private RandomVectorGenerator getRandomVectorizer() {
//        return new UnitSphereRandomVectorGenerator(variables.size());
//    }

    
    
}
