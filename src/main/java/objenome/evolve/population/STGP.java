/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.evolve.population;

import objenome.evolve.*;

import java.util.List;

/**
 * Static-typed Problem solvable by Evolution
 */
public abstract class STGP extends GP<STGPIndividual> {
    /**
     * The key for setting <code>Template</code> parameter.
     */
    public static final GPKey<STGP> PROBLEM = new GPKey<>();
    /**
     * The key -&gt; value mapping.
     */
    //private final HashMap<GPKey<?>, Object> properties = new HashMap<GPKey<?>, Object>(1);
    
    //private GPContainer config;

    /**
     * Constructs a new <code>Template</code>.
     */
    public STGP() {
        super();
        
        the(COMPONENTS, List.of(
                new Initializer(),
                new ScoreEvaluator(),
                new GenerationalStrategy(
            new BranchedBreeder(),
                        new ScoreEvaluator()
                )
        ));
        
    }
   public STGP(ScoreFunction f) {
        super();
        
        the(COMPONENTS, List.of(new Initializer(),
                new GenerationalStrategy(
                        new BranchedBreeder(),
                        new ScoreEvaluator(f))));
        
    }

}