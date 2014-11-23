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
import java.util.Map;

import objenome.evolve.Breeder;
import objenome.evolve.GPContainer.GPKey;
import objenome.evolve.BranchedBreeder;
import objenome.evolve.EvolutionaryStrategy;
import objenome.evolve.FitnessEvaluator;
import objenome.evolve.GenerationalStrategy;
import objenome.gene.gp.GenerationalTemplate;
import objenome.evolve.Initialiser;
import objenome.evolve.MaximumGenerations;
import objenome.evolve.Operator;
import objenome.evolve.Population;
import objenome.evolve.RandomSequence;
import objenome.evolve.TerminationCriteria;
import objenome.evolve.TerminationFitness;
import objenome.evolve.op.Node;
import objenome.evolve.op.Variable;
import objenome.evolve.op.VariableNode;
import objenome.evolve.op.bool.And;
import objenome.evolve.op.bool.Nor;
import objenome.evolve.op.bool.Or;
import objenome.evolve.fitness.DoubleFitness;
import objenome.evolve.random.MersenneTwisterFast;
import objenome.evolve.selection.TournamentSelector;
import objenome.evolve.STGPIndividual;
import objenome.evolve.fitness.HitsCount;
import objenome.evolve.init.Full;
import objenome.evolve.operator.SubtreeCrossover;
import objenome.evolve.operator.SubtreeMutation;
import objenome.evolve.tools.BenchmarkSolutions;
import objenome.evolve.tools.BooleanUtils;

/**
 * This template sets up EpochX to run the majority-3 benchmark with the STGP
 * representation. The majority-3 problem involves evolving a program which
 * receives an array of 3 boolean values. A program that solves the majority-3
 * problem will return true if more than half of the inputs are true, otherwise
 * it should return false.
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
 * <li>{@link STGPIndividual#SYNTAX}: <code>AndFunction</code>,
 * <code>OrFunction</code>, <code>NorFunction<code>,
 * <code>VariableNode("D0", Boolean)<code>, <code>VariableNode("D1", Boolean)<code>, <code>VariableNode("D2", Boolean)<code>
 * <li>{@link STGPIndividual#RETURN_TYPE}: <code>Boolean</code>
 * <li>{@link FitnessEvaluator#FUNCTION}: <code>HitsCount</code>
 * <li>{@link HitsCount#INPUT_VARIABLES}: <code>D0</code>, <code>D1</code>,
 * <code>D2</code>
 * <li>{@link HitsCount#INPUT_VALUE_SETS}: [all possible binary input
 * combinations]
 * <li>{@link HitsCount#EXPECTED_OUTPUTS}: [correct output for input value sets]
 *
 * @since 2.0
 */
public class STGPMajority3 extends GenerationalTemplate {

    private static final int NO_BITS = 3;

    /**
     * Sets up the given template with the benchmark config settings
     *
     * @param template a map to be filled with the template config
     */
    @Override
    protected void apply(Map<GPKey<?>, Object> template) {
        super.apply(template);

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
        List<Node> syntaxList = new ArrayList<Node>();
        syntaxList.add(new And());
        syntaxList.add(new Or());
        syntaxList.add(new Nor());

        Variable[] variables = new Variable[NO_BITS];
        for (int i = 0; i < NO_BITS; i++) {
            variables[i] = new Variable("D" + i, Boolean.class);
            syntaxList.add(new VariableNode(variables[i]));
        }

        Node[] syntax = syntaxList.toArray(new Node[syntaxList.size()]);

        template.put(STGPIndividual.SYNTAX, syntax);
        template.put(STGPIndividual.RETURN_TYPE, Boolean.class);

        // Generate inputs and expected outputs
        Boolean[][] inputValues = BooleanUtils.generateBoolSequences(NO_BITS);
        Boolean[] expectedOutputs = new Boolean[inputValues.length];
        for (int i = 0; i < inputValues.length; i++) {
            expectedOutputs[i] = BenchmarkSolutions.majority(inputValues[i]);
        }

        // Setup fitness function
        template.put(FitnessEvaluator.FUNCTION, new HitsCount());
        template.put(HitsCount.INPUT_VARIABLES, variables);
        template.put(HitsCount.INPUT_VALUE_SETS, inputValues);
        template.put(HitsCount.EXPECTED_OUTPUTS, expectedOutputs);
    }
}
