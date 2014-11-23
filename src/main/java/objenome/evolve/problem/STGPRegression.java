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
package objenome.evolve.problem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import objenome.evolve.op.Variable;
import objenome.evolve.op.VariableNode;
import objenome.evolve.op.math.Add;
import objenome.evolve.op.math.DivisionProtected;
import objenome.evolve.op.math.Multiply;
import objenome.evolve.op.math.Subtract;
import objenome.evolve.fitness.DoubleFitness;
import objenome.evolve.random.MersenneTwisterFast;
import objenome.evolve.selection.TournamentSelector;
import objenome.evolve.STGPIndividual;
import objenome.evolve.BranchedBreeder;
import objenome.evolve.Breeder;
import objenome.evolve.EvolutionaryStrategy;
import objenome.evolve.FitnessEvaluator;
import objenome.evolve.GenerationalStrategy;
import objenome.evolve.Initialiser;
import objenome.evolve.MaximumGenerations;
import objenome.evolve.Operator;
import objenome.evolve.Population;
import objenome.evolve.ProblemSTGP;
import objenome.evolve.RandomSequence;
import objenome.evolve.TerminationCriteria;
import objenome.evolve.TerminationFitness;
import objenome.evolve.fitness.HitsCount;
import objenome.evolve.init.Full;
import objenome.evolve.op.Node;
import objenome.evolve.operator.SubtreeCrossover;
import objenome.evolve.operator.SubtreeMutation;

/**
 * This template sets up EpochX to run the cubic regression benchmark with the
 * STGP representation. Cubic regression involves evolving an equivalent
 * function to the formula: x + x^2 + x^3
 *
 * The following configuration is used:
 *
 * <li>{@link Population#SIZE}: <code>100</code>
 * <li>{@link GenerationalStrategy#TERMINATION_CRITERIA}:
 * <code>MaximumGenerations</code>, <code>TerminationFitness(0.0)</code>
 * <li>{@link MaximumGenerations#MAXIMUM_GENERATIONS}: <code>50</code>
 * <li>{@link STGPIndividual#MAXIMUM_DEPTH}: <code>6</code>
 * <li>{@link BranchedBreeder#SELECTOR}: <code>TournamentSelector</code>
 * <li>{@link TournamentSelector#TOURNAMENT_SIZE}: <code>7</code>
 * <li>{@link Breeder#OPERATORS}: <code>SubtreeCrossover</code>,
 * <code>SubtreeMutation</code>
 * <li>{@link SubtreeMutation#PROBABILITY}: <code>0.0</code>
 * <li>{@link SubtreeCrossover#PROBABILITY}: <code>1.0</code>
 * <li>{@link Initialiser#METHOD}: <code>FullInitialisation</code>
 * <li>{@link RandomSequence#RANDOM_SEQUENCE}: <code>MersenneTwisterFast</code>
 * <li>{@link STGPIndividual#SYNTAX}: <code>AddFunction</code>,
 * <code>SubtractFunction</code>, <code>MultiplyFunction<code>,
 * <code>DivisionProtectedFunction<code>, <code>VariableNode("X", Double)<code>
 * <li>{@link STGPIndividual#RETURN_TYPE}: <code>Double</code>
 * <li>{@link FitnessEvaluator#FUNCTION}: <code>HitsCount</code>
 * <li>{@link HitsCount#POINT_ERROR}: <code>0.01</code>
 * <li>{@link HitsCount#INPUT_VARIABLES}: <code>X</code>
 * <li>{@link HitsCount#INPUT_VALUE_SETS}: [20 random values between -1.0 and
 * +1.0]
 * <li>{@link HitsCount#EXPECTED_OUTPUTS}: [correct output for input value sets]
 *
 * @since 2.0
 */
public class STGPRegression extends ProblemSTGP {

    int functionPoints = 20;
    
    /**
     * Sets up the given template with the benchmark config settings
     *
     * Function is evaluated at N points of func on -1..+1
     * 
     * @param template a map to be filled with the template config
     */    
    public STGPRegression(Function<Double,Double> func) {        
        super();
        
        the(Population.SIZE, 100);
        List<TerminationCriteria> criteria = new ArrayList<TerminationCriteria>();
        criteria.add(new TerminationFitness(new DoubleFitness.Minimise(0.0)));
        criteria.add(new MaximumGenerations());
        the(EvolutionaryStrategy.TERMINATION_CRITERIA, criteria);
        the(MaximumGenerations.MAXIMUM_GENERATIONS, 150);
        the(STGPIndividual.MAXIMUM_DEPTH, 6);

        the(Breeder.SELECTOR, new TournamentSelector());
        the(TournamentSelector.TOURNAMENT_SIZE, 7);
        List<Operator> operators = new ArrayList<Operator>();
        operators.add(new SubtreeCrossover());
        operators.add(new SubtreeMutation());
        the(Breeder.OPERATORS, operators);
        the(SubtreeCrossover.PROBABILITY, 1.0);
        the(SubtreeMutation.PROBABILITY, 0.0);
        the(Initialiser.METHOD, new Full());

        RandomSequence randomSequence = new MersenneTwisterFast();
        the(RandomSequence.RANDOM_SEQUENCE, randomSequence);

        // Setup syntax        
        the(STGPIndividual.SYNTAX, new Node[]{
            new Add(),
            new Subtract(),
            new Multiply(),
            new DivisionProtected(),
            new VariableNode( new Variable("X", Double.class) )
        });
        the(STGPIndividual.RETURN_TYPE, Double.class);

        // Generate inputs and expected outputs        
        Double[][] inputsGiven = new Double[functionPoints][1];
        Double[] outputsExpected = new Double[functionPoints];
        for (int i = 0; i < functionPoints; i++) {
            // Inputs values between -1.0 and +1.0
            inputsGiven[i][0] = (randomSequence.nextDouble() * 2) - 1;
            outputsExpected[i] = func.apply(inputsGiven[i][0]);
        }

        // Setup fitness function
        the(FitnessEvaluator.FUNCTION, new HitsCount());
        the(HitsCount.POINT_ERROR, 0.01);
        the(HitsCount.INPUT_VALUE_SETS, inputsGiven);
        the(HitsCount.EXPECTED_OUTPUTS, outputsExpected);
    }
}
