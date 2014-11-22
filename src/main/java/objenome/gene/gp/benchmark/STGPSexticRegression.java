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
package objenome.gene.gp.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import objenome.gene.gp.epochx.Breeder;
import objenome.gene.gp.epochx.Config.ConfigKey;
import objenome.gene.gp.epochx.BranchedBreeder;
import objenome.gene.gp.epochx.EvolutionaryStrategy;
import objenome.gene.gp.epochx.FitnessEvaluator;
import objenome.gene.gp.epochx.GenerationalStrategy;
import objenome.gene.gp.epochx.GenerationalTemplate;
import objenome.gene.gp.epochx.Initialiser;
import objenome.gene.gp.epochx.MaximumGenerations;
import objenome.gene.gp.epochx.Operator;
import objenome.gene.gp.epochx.Population;
import objenome.gene.gp.epochx.RandomSequence;
import objenome.gene.gp.epochx.TerminationCriteria;
import objenome.gene.gp.epochx.TerminationFitness;
import objenome.gene.gp.epox.Node;
import objenome.gene.gp.epox.Variable;
import objenome.gene.gp.epox.VariableNode;
import objenome.gene.gp.epox.math.Add;
import objenome.gene.gp.epox.math.DivisionProtected;
import objenome.gene.gp.epox.math.Multiply;
import objenome.gene.gp.epox.math.Subtract;
import objenome.gene.gp.fitness.DoubleFitness;
import objenome.gene.gp.random.MersenneTwisterFast;
import objenome.gene.gp.selection.TournamentSelector;
import objenome.gene.gp.STGPIndividual;
import objenome.gene.gp.fitness.HitsCount;
import objenome.gene.gp.init.Full;
import objenome.gene.gp.operator.SubtreeCrossover;
import objenome.gene.gp.operator.SubtreeMutation;
import objenome.gene.gp.tools.BenchmarkSolutions;

/**
 * This template sets up EpochX to run the sextic regression benchmark with
 * the STGP representation. Sextic regression involves evolving an equivalent
 * function to the formula: x^6 - (2 * x^4) + x^2
 * 
 * The following configuration is used:
 * 
 * <li>{@link Population#SIZE}: <code>100</code>
 * <li>{@link GenerationalStrategy#TERMINATION_CRITERIA}: <code>MaximumGenerations</code>, <code>TerminationFitness(0.0)</code>
 * <li>{@link MaximumGenerations#MAXIMUM_GENERATIONS}: <code>50</code>
 * <li>{@link STGPIndividual#MAXIMUM_DEPTH}: <code>6</code>
 * <li>{@link BranchedBreeder#SELECTOR}: <code>TournamentSelector</code>
 * <li>{@link TournamentSelector#TOURNAMENT_SIZE}: <code>7</code>
 * <li>{@link Breeder#OPERATORS}: <code>SubtreeCrossover</code>, <code>SubtreeMutation</code>
 * <li>{@link SubtreeMutation#PROBABILITY}: <code>0.0</code>
 * <li>{@link SubtreeCrossover#PROBABILITY}: <code>1.0</code>
 * <li>{@link Initialiser#METHOD}: <code>FullInitialisation</code>
 * <li>{@link RandomSequence#RANDOM_SEQUENCE}: <code>MersenneTwisterFast</code>
 * <li>{@link STGPIndividual#SYNTAX}: <code>AddFunction</code>, <code>SubtractFunction</code>, <code>MultiplyFunction<code>, 
 * <code>DivisionProtectedFunction<code>, <code>VariableNode("X", Double)<code>
 * <li>{@link STGPIndividual#RETURN_TYPE}: <code>Double</code>
 * <li>{@link FitnessEvaluator#FUNCTION}: <code>HitsCount</code>
 * <li>{@link HitsCount#POINT_ERROR}: <code>0.01</code>
 * <li>{@link HitsCount#INPUT_VARIABLES}: <code>X</code>
 * <li>{@link HitsCount#INPUT_VALUE_SETS}: [20 random values between -1.0 and +1.0]
 * <li>{@link HitsCount#EXPECTED_OUTPUTS}: [correct output for input value sets]
 * 
 * @since 2.0
 */
public class STGPSexticRegression extends GenerationalTemplate {

	/**
	 * Sets up the given template with the benchmark config settings
	 * 
	 * @param template a map to be filled with the template config
	 */
	@Override
	protected void fill(Map<ConfigKey<?>, Object> template) {
		super.fill(template);
		
        template.put(Population.SIZE, 100);
        List<TerminationCriteria> criteria = new ArrayList<TerminationCriteria>();
        criteria.add(new TerminationFitness(new DoubleFitness.Minimise(0.0)));
        criteria.add(new MaximumGenerations());
        template.put(EvolutionaryStrategy.TERMINATION_CRITERIA, criteria);
        template.put(MaximumGenerations.MAXIMUM_GENERATIONS, 50);
        template.put(STGPIndividual.MAXIMUM_DEPTH, 6);
        
        template.put(Breeder.SELECTOR, new TournamentSelector());
        template.put(TournamentSelector.TOURNAMENT_SIZE, 7);        
        List<Operator> operators = new ArrayList<Operator>();
        operators.add(new SubtreeCrossover());
        operators.add(new SubtreeMutation());
        template.put(Breeder.OPERATORS, operators);
        template.put(SubtreeCrossover.PROBABILITY, 1.0);
        template.put(SubtreeMutation.PROBABILITY, 0.0);
        template.put(Initialiser.METHOD, new Full());
        
        RandomSequence randomSequence = new MersenneTwisterFast();
        template.put(RandomSequence.RANDOM_SEQUENCE, randomSequence);
        
        // Setup syntax
        Variable varX = new Variable("X", Double.class);
		Node[] syntax = new Node[]{
			new Add(),
			new Subtract(),
			new Multiply(),
			new DivisionProtected(),
			new VariableNode(varX)
		};
        template.put(STGPIndividual.SYNTAX, syntax);
        template.put(STGPIndividual.RETURN_TYPE, Double.class);
        
        // Generate inputs and expected outputs
        int noPoints = 20;
        Double[][] inputs = new Double[noPoints][1];
        Double[] expectedOutputs = new Double[noPoints];
        for (int i=0; i<noPoints; i++) {
        	// Inputs values between -1.0 and +1.0
        	inputs[i][0] = (randomSequence.nextDouble() * 2) - 1;
        	expectedOutputs[i] = BenchmarkSolutions.sexticRegression(inputs[i][0]);
        }
        
        // Setup fitness function
        template.put(FitnessEvaluator.FUNCTION, new HitsCount());
        template.put(HitsCount.POINT_ERROR, 0.01);
        template.put(HitsCount.INPUT_VARIABLES, new Variable[]{varX});
        template.put(HitsCount.INPUT_VALUE_SETS, inputs);
        template.put(HitsCount.EXPECTED_OUTPUTS, expectedOutputs);
	}
}
