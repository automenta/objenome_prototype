/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.evolve;

import com.google.common.collect.Lists;

/**
 * Static-typed Problem solvable by Evolution
 */
public abstract class ProblemSTGP extends GPContainer<STGPIndividual> {
    /**
     * The key for setting <code>Template</code> parameter.
     */
    public static final GPKey<ProblemSTGP> PROBLEM = new GPKey<>();
    /**
     * The key -&gt; value mapping.
     */
    //private final HashMap<GPKey<?>, Object> properties = new HashMap<GPKey<?>, Object>(1);
    
    //private GPContainer config;

    /**
     * Constructs a new <code>Template</code>.
     */
    public ProblemSTGP() {
        super();
        
        the(COMPONENTS, Lists.newArrayList(new Component[] {
            new Initialiser(),
            new FitnessEvaluator(),
            new GenerationalStrategy(new BranchedBreeder(), new FitnessEvaluator())            
        }));
        
    }


}
