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
package objenome.solver.evolve.fitness;

import objenome.solver.evolve.GPContainer;
import objenome.solver.evolve.GPContainer.GPKey;
import objenome.solver.evolve.Individual;
import objenome.solver.evolve.Population;
import objenome.solver.evolve.STGPIndividual;
import objenome.solver.evolve.event.ConfigEvent;
import objenome.solver.evolve.event.Listener;
import objenome.op.Variable;

/**
 * A fitness function for <code>STGPIndividual</code>s that calculates and
 * assigns <code>DoubleFitness.Minimise</code> scores. The fitness scores are
 * calculated by executing the program for each of the provided sets of inputs.
 * The difference between the value returned by the program and the expected
 * outputs supplied is summed to give the fitness value.
 *
 * When using this fitness function the
 * {@link #INPUT_VARIABLES}, {@link #INPUT_VALUE_SETS} and
 * {@link #EXPECTED_OUTPUTS} config options must be set, or the same values set
 * using the mutator methods provided. The length of the
 * <code>INPUT_VALUE_SETS</code> array should match the length of the
 * <code>EXPECTED_OUTPUTS</code> array and the number of values in each set
 * should match the length of the <code>INPUT_VARIABLES</code> array.
 *
 * If the program returns <code>NaN</code> for any of the input sets then a
 * fitness score of <code>NaN</code> is assigned by default, although this can
 * be changed by overriding the <code>nanFitnessScore</code> method.
 *
 * @since 2.0
 */
public class SumOfError extends STGPFitnessFunction implements Listener<ConfigEvent> {

    /**
     * The key for setting the program's input variables
     */
    public static final GPKey<Variable[]> INPUT_VARIABLES = new GPKey<>();

    /**
     * The key for setting the sets of values to use as inputs. The length of
     * the array should match the length of the EXPECTED_OUTPUTS array and the
     * number of values in each set should match the length of the
     * INPUT_VARIABLES array.
     */
    public static final GPKey<Object[][]> INPUT_VALUE_SETS = new GPKey<>();

    /**
     * The key for setting the expected output values from the programs being
     * evaluated
     */
    public static final GPKey<Double[]> EXPECTED_OUTPUTS = new GPKey<>();

    // Configuration settings
    private Variable[] inputVariables;
    private Object[][] inputValueSets;
    private Double[] expectedOutputs;
    private final boolean autoConfig;

    /**
     * Constructs a <code>SumOfError</code> fitness function with control
     * parameters automatically loaded from the config.
     */
    public SumOfError() {
        this(true);
    }

    /**
     * Constructs a <code>SumOfError</code> fitness function with control
     * parameters initially loaded from the config. If the
     * <code>autoConfig</code> argument is set to <code>true</code> then the
     * configuration will be automatically updated when the config is modified.
     *
     * @param autoConfig whether this operator should automatically update its
     * configuration settings from the config
     */
    public SumOfError(boolean autoConfig) {
        this.autoConfig = autoConfig;
    }

    /**
     * Sets up this operator with the appropriate configuration settings. This
     * method is called whenever a <code>ConfigEvent</code> occurs for a change
     * in any of the following configuration parameters:
     * <ul>
     * <li>{@link #INPUT_VARIABLES}
     * <li>{@link #INPUT_VALUE_SETS}
     * <li>{@link #EXPECTED_OUTPUTS}
     * </ul>
     */
    public void setConfig(GPContainer config) {
        if (autoConfig) {
            config.on(ConfigEvent.class, this);
        }

        inputVariables = config.get(INPUT_VARIABLES);
        inputValueSets = config.get(INPUT_VALUE_SETS);
        expectedOutputs = config.get(EXPECTED_OUTPUTS);
    }

    /**
     * Receives configuration events and triggers this fitness function to
     * configure its parameters if the <code>ConfigEvent</code> is for one of
     * its required parameters.
     *
     * @param event {@inheritDoc}
     */
    @Override
    public void onEvent(ConfigEvent event) {
        if (event.isKindOf(/*PROBLEM, */INPUT_VARIABLES, INPUT_VALUE_SETS, EXPECTED_OUTPUTS)) {
            setConfig(event.getConfig());
        }
    }

    /**
     * Calculates the fitness of the given individual. This fitness function
     * only operates on STGPIndividuals with a Double data-type. The fitness
     * returned will be an instance of DoubleFitness.Minimise. The fitness score
     * is calculated as the sum of the difference between the expected outputs
     * and the actual outputs, for each set of inputs.
     *
     * @param individual the individual to evaluate the fitness of
     * @return the fitness of the given individual
     * @throws IllegalArgumentException if the individual is not an
     * STGPIndividual or the individual's data-type is not Double.
     */
    @Override
    public DoubleFitness.Minimise evaluate(Population population, Individual individual) {

        if (!(individual instanceof STGPIndividual)) {
            throw new IllegalArgumentException("Unsupported representation");
        }

        setConfig(population.getConfig());

        //TODO validate number of inputs etc
        STGPIndividual program = (STGPIndividual) individual;

        if (program.dataType() != Double.class) {
            throw new IllegalArgumentException("Unsupported data-type");
        }

        Object[] outputs = new Object[expectedOutputs.length];
        for (int i = 0; i < inputValueSets.length; i++) {
            // Update the variable values
            for (int j = 0; j < inputVariables.length; j++) {
                inputVariables[j].setValue(inputValueSets[i][j]);
            }

            // Run the program
            outputs[i] = program.evaluate();
        }

        // Sum the difference between expected and actual
        Double errorSum = 0.0;
        for (int i = 0; i < outputs.length; i++) {
            Object result = outputs[i];

            if (result instanceof Double) {
                double d = (Double) result;

                if (!Double.isNaN(d)) {
                    double error = Math.abs(d - expectedOutputs[i]);
                    errorSum += error;
                } else {
                    errorSum = nanFitnessScore();
                    break;
                }
            }
        }

        return new DoubleFitness.Minimise(errorSum);
    }

    /**
     * Returns the value to be used when an individual returns a NaN value. The
     * default value is <code>Double.NaN</code>.
     *
     * @return the value to return when an individual returns a NaN value
     */
    protected Double nanFitnessScore() {
        return Double.NaN;
    }

    /**
     * Gets the input variables that are currently set
     *
     * @return the current input variables
     */
    public Variable[] getInputVariables() {
        return inputVariables;
    }

    /**
     * Sets the input variables. These should be the variables used in the
     * terminal set, which will have the input values assigned to them.
     *
     * If automatic configuration is enabled then any value set here will be
     * overwritten by the {@link #INPUT_VARIABLES} configuration setting on the
     * next config event.
     *
     * @param inputVariables the input variables
     */
    public void setInputVariables(Variable[] inputVariables) {
        this.inputVariables = inputVariables;
    }

    /**
     * Returns the sets of input values.
     *
     * @return the sets of input values
     */
    public Object[][] getInputValueSets() {
        return inputValueSets;
    }

    /**
     * Sets the sets of input values. The length of the array should match the
     * length of the expected outputs array. Each set of values should have the
     * same number of values, equal to the length of the input variables array.
     *
     * If automatic configuration is enabled then any value set here will be
     * overwritten by the {@link #INPUT_VALUE_SETS} configuration setting on the
     * next config event.
     *
     * @param inputValueSets the sets of input values
     */
    public void setInputValueSets(Object[][] inputValueSets) {
        this.inputValueSets = inputValueSets;
    }

    /**
     * Returns the expected outputs that the actual outputs will be compared
     * against
     *
     * @return the expected outputs for the input sets
     */
    public Double[] getExpectedOutputs() {
        return expectedOutputs;
    }

    /**
     * Sets the expected outputs to compare against. The length of the array
     * should match the length of the input values array.
     *
     * If automatic configuration is enabled then any value set here will be
     * overwritten by the {@link #EXPECTED_OUTPUTS} configuration setting on the
     * next config event.
     *
     * @param expectedOutputs the expected outputs to compare against
     */
    public void setExpectedOutputs(Double[] expectedOutputs) {
        this.expectedOutputs = expectedOutputs;
    }

}
