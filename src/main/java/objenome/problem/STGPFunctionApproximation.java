/* 
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http:/www.epochx.org
 */
package objenome.problem;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import objenome.solver.evolve.Breeder;
import objenome.solver.evolve.EvolutionaryStrategy;
import objenome.solver.evolve.FitnessEvaluator;
import objenome.solver.evolve.Initialiser;
import objenome.solver.evolve.MaximumGenerations;
import objenome.solver.evolve.Operator;
import objenome.solver.evolve.Population;
import objenome.solver.evolve.RandomSequence;
import objenome.solver.evolve.STGPIndividual;
import objenome.solver.evolve.TerminationCriteria;
import objenome.solver.evolve.TerminationFitness;
import objenome.solver.evolve.fitness.DoubleFitness;
import objenome.op.Node;
import objenome.op.Variable;
import objenome.op.VariableNode;
import objenome.op.math.Add;
import objenome.op.math.DivisionProtected;
import objenome.op.math.Multiply;
import objenome.op.math.Subtract;
import objenome.solver.evolve.fitness.SumOfError;
import objenome.solver.evolve.init.Full;
import objenome.solver.evolve.mutate.SubtreeCrossover;
import objenome.solver.evolve.mutate.SubtreeMutation;
import objenome.util.random.MersenneTwisterFast;
import objenome.solver.evolve.selection.TournamentSelector;

/**
 * Evolves a function that minimizes the total error of an expression
 * evaluated according to a set of sampled points.
 * @since 2.0
 */
public class STGPFunctionApproximation extends ProblemSTGP {

    
    public final Variable x;
    public final Deque<Observation<Double[], Double>> samples;
    
 
    public STGPFunctionApproximation(int populationSize) {        
        super();
        
        the(Population.SIZE, populationSize);
        List<TerminationCriteria> criteria = new ArrayList<>();
        criteria.add(new TerminationFitness(new DoubleFitness.Minimise(0.0)));
        criteria.add(new MaximumGenerations());
        the(EvolutionaryStrategy.TERMINATION_CRITERIA, criteria);
        the(MaximumGenerations.MAXIMUM_GENERATIONS, 150);
        the(STGPIndividual.MAXIMUM_DEPTH, 6);

        the(Breeder.SELECTOR, new TournamentSelector());
        the(TournamentSelector.TOURNAMENT_SIZE, 7);
        List<Operator> operators = new ArrayList<>();
        operators.add(new SubtreeCrossover());
        operators.add(new SubtreeMutation());
        the(Breeder.OPERATORS, operators);
        the(SubtreeCrossover.PROBABILITY, 1.0);
        the(SubtreeMutation.PROBABILITY, 0.0);
        the(Initialiser.METHOD, new Full());
        //the(Initialiser.METHOD, new RampedHalfAndHalf());

        RandomSequence randomSequence = new MersenneTwisterFast();
        the(RandomSequence.RANDOM_SEQUENCE, randomSequence);

        // Setup syntax        
        the(STGPIndividual.SYNTAX, new Node[]{
            
            new Add(),
            new Subtract(),
            new Multiply(),
            new DivisionProtected(),
            
            //...
            
            new VariableNode( x = new Variable("X", Double.class) )
        });
        the(STGPIndividual.RETURN_TYPE, Double.class);
        
        SumOfError<Double,Double> fitness;


        // Setup fitness function
        the(FitnessEvaluator.FUNCTION, fitness = new SumOfError<Double,Double>());

        samples = fitness.obs;
    }
    
}
