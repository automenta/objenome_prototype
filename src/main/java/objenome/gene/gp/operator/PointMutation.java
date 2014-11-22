/*
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
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
 * The latest version is available from: http://www.epochx.org
 */
package objenome.gene.gp.operator;

import static objenome.gene.gp.STProblem.PROBLEM;
import static objenome.gene.gp.RandomSequence.RANDOM_SEQUENCE;
import static objenome.gene.gp.STGPIndividual.SYNTAX;

import java.util.ArrayList;
import java.util.List;

import objenome.gene.gp.AbstractOperator;
import objenome.gene.gp.GPContainer;
import objenome.gene.gp.GPContainer.ConfigKey;
import objenome.gene.gp.Individual;
import objenome.gene.gp.RandomSequence;
import objenome.gene.gp.op.Node;
import objenome.gene.gp.event.ConfigEvent;
import objenome.gene.gp.event.Listener;
import objenome.gene.gp.event.OperatorEvent;
import objenome.gene.gp.event.OperatorEvent.EndOperator;
import objenome.gene.gp.STGPIndividual;

/**
 * A mutation operator for <code>STGPIndividual</code>s that replaces nodes at
 * random throughout a program tree. Each node in the program tree is replaced
 * according to a set point probability. When a node is selected to be replaced,
 * a node of the same arity and data-type requirements is randomly chosen to
 * replace it. Multiple nodes may be mutated in the same program tree.
 *
 * <p>
 * See the {@link #setup()} method documentation for a list of configuration
 * parameters used to control this operator.
 *
 * @see SubtreeMutation
 *
 * @since 2.0
 */
public class PointMutation extends AbstractOperator implements Listener<ConfigEvent> {

    /**
     * The key for setting and retrieving the probability of each node being
     * mutated
     */
    public static final ConfigKey<Double> POINT_PROBABILITY = new ConfigKey<Double>();

    /**
     * The key for setting and retrieving the probability of this operator being
     * applied
     */
    public static final ConfigKey<Double> PROBABILITY = new ConfigKey<Double>();

    // Configuration settings
    private Node[] syntax;
    private RandomSequence random;
    private Double pointProbability;
    private Double probability;
    private final boolean autoConfig;

    /**
     * Constructs a <code>PointMutation</code> with control parameters
     * automatically loaded from the config
     */
    public PointMutation() {
        this(true);
    }

    /**
     * Constructs a <code>PointMutation</code> with control parameters initially
     * loaded from the config. If the <code>autoConfig</code> argument is set to
     * <code>true</code> then the configuration will be automatically updated
     * when the config is modified.
     *
     * @param autoConfig whether this operator should automatically update its
     * configuration settings from the config
     */
    public PointMutation(boolean autoConfig) {
        // Default config values
        pointProbability = 0.01;
        this.autoConfig = autoConfig;

    }

    /**
     * Sets up this operator with the appropriate configuration settings. This
     * method is called whenever a <code>ConfigEvent</code> occurs for a change
     * in any of the following configuration parameters:
     * <ul>
     * <li>{@link RandomSequence#RANDOM_SEQUENCE}
     * <li>{@link STGPIndividual#SYNTAX}
     * <li>{@link #POINT_PROBABILITY} (defaults to <code>0.01</code>).
     * <li>{@link #PROBABILITY}
     * </ul>
     */
    public void setConfig(GPContainer config) {
        if (autoConfig) {
            config.on(ConfigEvent.class, this);
        }
        random = config.get(RANDOM_SEQUENCE);
        syntax = config.get(SYNTAX);
        pointProbability = config.get(POINT_PROBABILITY, pointProbability);
        probability = config.get(PROBABILITY);
    }

    /**
     * Receives configuration events and triggers this operator to configure its
     * parameters if the <code>ConfigEvent</code> is for one of its required
     * parameters.
     *
     * @param event {@inheritDoc}
     */
    @Override
    public void onEvent(ConfigEvent event) {
        if (event.isKindOf(PROBLEM, RANDOM_SEQUENCE, SYNTAX, POINT_PROBABILITY, PROBABILITY)) {
            setConfig(event.getConfig());
        }
    }

    /**
     * Performs point mutation on the given individual. Each node in the program
     * tree is considered for mutation and is selected with probability set by
     * the point probability. Once selected, a new node of the same arity and
     * data-type requirements is randomly chosen and replaces the node at that
     * mutation point. The mutation continues until all nodes in the tree have
     * been considered.
     *
     * @param parents an array of just one individual to undergo subtree
     * mutation. It must be an instance of <code>STGPIndividual</code>.
     * @return an array containing one <code>STGPIndividual</code> that was the
     * result of mutating the parent individual
     */
    @Override
    public STGPIndividual[] perform(EndOperator event, Individual... parents) {
        STGPIndividual program = (STGPIndividual) parents[0];
        STGPIndividual child = program.clone();

        List<Integer> points = new ArrayList<Integer>();

        //TODO It would be more efficient to traverse the tree than use getNode
        int length = program.length();
        for (int i = 0; i < length; i++) {
            if (random.nextDouble() < pointProbability) {
                Node node = program.getNode(i);
                int arity = node.getArity();

                List<Node> replacements = validReplacements(node);
                if (!replacements.isEmpty()) {
                    // Randomly choose a replacement.
                    Node replacement = replacements.get(random.nextInt(replacements.size()));
                    replacement = replacement.newInstance();

                    // Attach the old node's children.
                    for (int k = 0; k < arity; k++) {
                        replacement.setChild(k, node.getChild(k));
                    }

                    child.setNode(i, replacement);
                    points.add(i);
                }
            }
        }

        ((EndEvent) event).setMutationPoints(points);

        return new STGPIndividual[]{child};
    }

    /**
     * Returns a <code>PointMutationEndEvent</code> with the operator and
     * parents set
     */
    @Override
    protected EndEvent getEndEvent(Individual... parents) {
        return new EndEvent(this, parents);
    }

    /**
     * Lists the nodes in the syntax that are valid replacements for the given
     * node <code>n</code>. A node is a valid replacement if it has the same
     * arity and a compatible data-type if given the children of <code>n</code>.
     *
     * @param n the node to be replaced
     * @return a list of the nodes that are valid replacements for
     * <code>n</code>
     */
    protected List<Node> validReplacements(Node n) {
        int arity = n.getArity();

        // TODO This should be the parent's required argument type
        Class<?> requiredType = n.dataType();

        // Get the data-type of children
        Class<?>[] argTypes = new Class<?>[arity];
        for (int i = 0; i < arity; i++) {
            argTypes[i] = n.getChild(i).getClass();
        }

        // Filter the syntax down to valid replacements
        List<Node> replacements = new ArrayList<Node>();
        for (Node replacement : syntax) {
            if ((replacement.getArity() == arity) && !nodesEqual(replacement, n)) {
                Class<?> replacementReturn = replacement.dataType(argTypes);
                if ((replacementReturn != null) && requiredType.isAssignableFrom(replacementReturn)) {
                    replacements.add(replacement);
                }
            }
        }

        return replacements;
    }

    /*
     * Helper function to check node equivalence. We cannot just use Node's
     * equals() method because we don't want to compare children if it's a
     * non-terminal node.
     */
    private boolean nodesEqual(final Node nodeA, final Node nodeB) {
        boolean equal = false;
        if (nodeA.getClass().equals(nodeB.getClass())) {
            if (nodeA.getArity() > 0) {
                equal = true;
            } else {
                equal = nodeA.equals(nodeB);
            }
        }

        return equal;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Point mutation operates on 1 individual.
     *
     * @return {@inheritDoc}
     */
    @Override
    public int inputSize() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double probability() {
        return probability;
    }

    /**
     * Sets the probability of this operator being selected
     *
     * @param probability the new probability to set
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * Returns the random number sequence in use
     *
     * @return the currently set random sequence
     */
    public RandomSequence getRandomSequence() {
        return random;
    }

    /**
     * Sets the random number sequence to use. If automatic configuration is
     * enabled then any value set here will be overwritten by the
     * {@link RandomSequence#RANDOM_SEQUENCE} configuration setting on the next
     * config event.
     *
     * @param random the random number generator to set
     */
    public void setRandomSequence(RandomSequence random) {
        this.random = random;
    }

    /**
     * Returns the array of nodes in the available syntax. Replacement nodes are
     * selected from the nodes in this array.
     *
     * @return an array of the nodes in the syntax
     */
    public Node[] getSyntax() {
        return syntax;
    }

    /**
     * Sets the array of nodes to generate replacement subtrees from. If
     * automatic configuration is enabled then any value set here will be
     * overwritten by the {@link STGPIndividual#SYNTAX} configuration setting on
     * the next config event.
     *
     * @param syntax an array of nodes to generate new program trees from
     */
    public void setSyntax(Node[] syntax) {
        this.syntax = syntax;
    }

    /**
     * Returns the probability that any given node will be replaced
     *
     * @return the probability that a node will be replaced
     */
    public double getPointProbability() {
        return pointProbability;
    }

    /**
     * Sets the probability any given node will be replaced. If automatic
     * configuration is enabled then any value set here will be overwritten by
     * the {@link PointMutation#POINT_PROBABILITY} configuration setting on the
     * next config event.
     *
     * @param pointProbability the probability that a node will be replaced
     */
    public void setPointProbability(double pointProbability) {
        this.pointProbability = pointProbability;
    }

    /**
     * An event fired at the end of a point mutation
     *
     * @see PointMutation
     *
     * @since 2.0
     */
    public class EndEvent extends OperatorEvent.EndOperator {

        private List<Integer> points;

        /**
         * Constructs a <code>SubtreeMutationEndEvent</code> with the details of
         * the event
         *
         * @param operator the operator that performed the mutation
         * @param parent the individual that the operator was performed on
         *
         */
        public EndEvent(PointMutation operator, Individual... parents) {
            super(operator, parents);
        }

        /**
         * Returns a list of the mutation points in the parent program tree
         *
         * @return a list of indices of the mutation points
         */
        public List<Integer> getMutationPoints() {
            return points;
        }

        /**
         * Sets the points that were mutated in the parent program tree
         *
         * @param points a list of indices of the mutation points
         */
        public void setMutationPoints(List<Integer> points) {
            this.points = points;
        }
    }
}
