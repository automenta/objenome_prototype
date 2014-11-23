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

import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
import java.util.List;

import objenome.evolve.Breeder;
import objenome.evolve.BranchedBreeder;
import objenome.evolve.EvolutionaryStrategy;
import objenome.evolve.FitnessEvaluator;
import objenome.evolve.GenerationalStrategy;
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
import objenome.evolve.op.bool.Not;
import objenome.evolve.op.bool.Or;
import objenome.evolve.op.lang.If;
import objenome.evolve.fitness.DoubleFitness;
import objenome.evolve.random.MersenneTwisterFast;
import objenome.evolve.selection.TournamentSelector;
import objenome.evolve.STGPIndividual;
import objenome.evolve.ProblemSTGP;
import objenome.evolve.fitness.HitsCount;
import objenome.evolve.init.Full;
import objenome.evolve.operator.SubtreeCrossover;
import objenome.evolve.operator.SubtreeMutation;
import objenome.evolve.tools.BenchmarkSolutions;
import objenome.evolve.tools.BooleanUtils;

/**
 * This template sets up EpochX to run the 6-bit multiplexer benchmark with the
 * STGP representation. The 6-bit multiplexer problem involves evolving a
 * program which receives an array of 6 boolean values. The first 2 values are
 * address bits, which the program should convert into an index for which of the
 * remaining data registers to return. {a0, a1, d0, d1, d2, d3}.
 *
 * <table>
 * <tr>
 * <td>a0</td><td>a1</td><td>return value</td>
 * <td>false</td><td>false</td><td>d0</td>
 * <td>true</td><td>false</td><td>d1</td>
 * <td>false</td><td>true</td><td>d2</td>
 * <td>true</td><td>true</td><td>d3</td>
 * </tr>
 * </table>
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
 * <code>OrFunction</code>, <code>NotFunction<code>,
 * <code>IfFunction<code>, <code>VariableNode("A0", Boolean)<code>, <code>VariableNode("A1", Boolean)<code>,
 * <code>VariableNode("D2", Boolean)<code>, <code>VariableNode("D3", Boolean)<code>, <code>VariableNode("D4", Boolean)<code>,
 * <code>VariableNode("D5", Boolean)<code>
 * <li>{@link STGPIndividual#RETURN_TYPE}: <code>Boolean</code>
 * <li>{@link FitnessEvaluator#FUNCTION}: <code>HitsCount</code>
 * <li>{@link HitsCount#INPUT_VARIABLES}: <code>A0</code>, <code>A1</code>,
 * <code>D2</code>, <code>D3</code>, <code>D4</code>, <code>D5</code>
 * <li>{@link HitsCount#INPUT_VALUE_SETS}: [all possible binary input
 * combinations]
 * <li>{@link HitsCount#EXPECTED_OUTPUTS}: [correct output for input value sets]
 *
 * @since 2.0
 */
public class STGPMultiplexer extends ProblemSTGP {

    final int BITS;

    public STGPMultiplexer(int bits) {
        super();
        
        this.BITS = bits;
                
        int noAddressBits = BenchmarkSolutions.multiplexerAddressBits(BITS);

        the(Population.SIZE, 100);
        
        
        the(EvolutionaryStrategy.TERMINATION_CRITERIA, newArrayList(new TerminationCriteria[] {
            new TerminationFitness(new DoubleFitness.Minimise(0.0)),
            new MaximumGenerations()
        }));
        
        the(MaximumGenerations.MAXIMUM_GENERATIONS, 50);
        the(STGPIndividual.MAXIMUM_DEPTH, 6);

        the(Breeder.SELECTOR, new TournamentSelector());
        the(TournamentSelector.TOURNAMENT_SIZE, 7);

        the(SubtreeCrossover.PROBABILITY, 1.0);
        the(SubtreeMutation.PROBABILITY, 0.0);
        
        the(Breeder.OPERATORS, newArrayList(new Operator[] {
            new SubtreeCrossover(),
            new SubtreeMutation()           
        }));
        
        the(Initialiser.METHOD, new Full());
        ;
        the(RandomSequence.RANDOM_SEQUENCE, new MersenneTwisterFast());

        // Setup syntax
        List<Node> syntaxList = new ArrayList<Node>();
        syntaxList.add(new And());
        syntaxList.add(new Or());
        syntaxList.add(new Not());
        syntaxList.add(new If());

        Variable[] variables = new Variable[BITS];

        for (int i = 0; i < noAddressBits; i++) {
            variables[i] = new Variable("A" + i, Boolean.class);
            syntaxList.add(new VariableNode(variables[i]));
        }
        for (int i = noAddressBits; i < BITS; i++) {
            variables[i] = new Variable("D" + i, Boolean.class);
            syntaxList.add(new VariableNode(variables[i]));
        }

        the(STGPIndividual.SYNTAX, syntaxList.toArray(new Node[syntaxList.size()]));
        the(STGPIndividual.RETURN_TYPE, Boolean.class);

        // Generate inputs and expected outputs
        Boolean[][] inputValues = BooleanUtils.generateBoolSequences(BITS);
        Boolean[] expectedOutputs = new Boolean[inputValues.length];
        for (int i = 0; i < inputValues.length; i++) {
            expectedOutputs[i] = BenchmarkSolutions.multiplexer(inputValues[i], noAddressBits);
        }

        // Setup fitness function
        the(FitnessEvaluator.FUNCTION, new HitsCount());
        the(HitsCount.INPUT_VARIABLES, variables);
        the(HitsCount.INPUT_VALUE_SETS, inputValues);
        the(HitsCount.EXPECTED_OUTPUTS, expectedOutputs);
    }
}
