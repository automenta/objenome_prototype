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
package objenome.evolve.population;

import objenome.evolve.*;
import objenome.evolve.init.Full;
import objenome.evolve.mutate.SubtreeCrossover;
import objenome.evolve.mutate.SubtreeMutation;
import objenome.evolve.score.DoubleScore;
import objenome.evolve.score.HitsCount;
import objenome.evolve.selection.TournamentSelector;
import objenome.op.Node;
import objenome.op.Variable;
import objenome.op.VariableNode;
import objenome.op.math.Add;
import objenome.op.math.DivisionProtected;
import objenome.op.math.Multiply;
import objenome.op.math.Subtract;
import objenome.util.random.MersenneTwisterFast;

import java.util.List;
import java.util.function.Function;

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
 * <li>{@link Initializer#METHOD}: <code>FullInitialisation</code>
 * <li>{@link RandomSequence#RANDOM_SEQUENCE}: <code>MersenneTwisterFast</code>
 * <li>{@link STGPIndividual#SYNTAX}: <code>AddFunction</code>,
 * <code>SubtractFunction</code>, <code>MultiplyFunction<code>,
 * <code>DivisionProtectedFunction<code>, <code>VariableNode("X", Double)<code>
 * <li>{@link STGPIndividual#RETURN_TYPE}: <code>Double</code>
 * <li>{@link ScoreEvaluator#FUNCTION}: <code>HitsCount</code>
 * <li>{@link HitsCount#POINT_ERROR}: <code>0.01</code>
 * <li>{@link HitsCount#INPUT_VARIABLES}: <code>X</code>
 * <li>{@link HitsCount#INPUT_VALUE_SETS}: [20 random values between -1.0 and
 * +1.0]
 * <li>{@link HitsCount#EXPECTED_OUTPUTS}: [correct output for input value sets]
 *
 * @since 2.0
 */
public class STGPRegression extends STGP {

    final int functionPoints;
    
    public final Variable x;
    
    /**
     * Sets up the given template with the benchmark config settings
     *
     * Function is evaluated at N points of func on -1..+1
     * 
     * @param template a map to be filled with the template config
     */    
    public STGPRegression(int functionSamples, Function<Double,Double> func) {
        super();
        this.functionPoints = functionSamples;
        
        the(Population.SIZE, 100);

        the(STGPIndividual.SYNTAX, new Node[]{
            new Add(),
            new Subtract(),
            new Multiply(),
            new DivisionProtected(),
            new VariableNode( x = new Variable("X", Double.class) )
        });
        the(STGPIndividual.RETURN_TYPE, Double.class);
        the(STGPIndividual.MAXIMUM_DEPTH, 5);

        the(EvolutionaryStrategy.TERMINATION_CRITERIA, List.of(
            new TerminationScore(new DoubleScore.Minimize(0)),
            new MaximumGenerations()
        ));
        the(MaximumGenerations.MAXIMUM_GENERATIONS, 150);

        the(TournamentSelector.TOURNAMENT_SIZE, 7);

        the(Breeder.SELECTOR, new TournamentSelector());
        the(Breeder.OPERATORS, List.of(
            new SubtreeCrossover(),
            new SubtreeMutation())
        );

        the(Initializer.METHOD, new Full());

        the(SubtreeCrossover.PROBABILITY, 1.0);
        the(SubtreeMutation.PROBABILITY, 0.1);

        RandomSequence randomSequence = new MersenneTwisterFast();
        the(RandomSequence.RANDOM_SEQUENCE, randomSequence);

        // Generate inputs and expected outputs        
        Double[][] x = new Double[functionSamples][1];
        Double[] y = new Double[functionSamples];
        for (int i = 0; i < functionSamples; i++) {
            // range: -1.0..+1.0
            double xi = (randomSequence.nextDouble() * 2) - 1;

            y[i] = func.apply((x[i][0] = xi));

        }
        the(HitsCount.INPUT_VALUE_SETS, x);
        the(HitsCount.EXPECTED_OUTPUTS, y);

        the(ScoreEvaluator.FUNCTION, new HitsCount());
        the(HitsCount.POINT_ERROR, 0.01);

    }
}