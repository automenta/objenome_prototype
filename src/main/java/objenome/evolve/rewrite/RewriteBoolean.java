/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package objenome.evolve.rewrite;

import objenome.evolve.STGPIndividual;
import objenome.op.Literal;
import objenome.op.Node;
import objenome.op.bool.And;

/**
 * Basic rewrite rules for boolean expressions.
 */
public class RewriteBoolean implements RewriteRule {

    /**
     * True, if the value has been rewritten.
     */
    private boolean rewritten;

    /**
     * Returns true, if the specified constant value is a true const. Returns
     * false in any other case.
     *
     * @param node The node to check.
     * @return True if the value is a true const.
     */
    private static int polarity(Node node) {
        Object v = node.evaluate();
        if (v instanceof Boolean) {
            return ((Boolean) v) ?  +1 : -1;
        } else if (v instanceof Number) {
            return ((Number) v).doubleValue() != 0 ? +1 : -1;
        }
        return 0;
    }

//    /**
//     * Returns true, if the specified constant value is a false const. Returns
//     * false in any other case.
//     *
//     * @param node The node to check.
//     * @return True if the value is a false const.
//     */
//    private static boolean isFalse(Node node) {
//        return !isTrue(node);
//        /*
//         if (node.getTemplate() == StandardExtensions.EXTENSION_CONST_SUPPORT) {
//         ExpressionValue v = node.evaluate();
//         if (v.isBoolean()) {
//         if (!v.toBooleanValue()) {
//         return true;
//         }
//         }
//         }
//         return false;
//         */
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean rewrite(final STGPIndividual program) {
        this.rewritten = false;


        Node rewrittenRoot = internalRewrite(program.root());
        if (rewrittenRoot != null)
            program.setRoot(rewrittenRoot);

        return this.rewritten;
    }

    /**
     * Attempt to rewrite the specified node.
     *
     * @param parent The node to attempt to rewrite.
     * @return The rewritten node, or the original node, if no change was made.
     */
    private Node internalRewrite(final Node parent) {
        if (!parent.allConstDescendants())
            return parent;

        Node rewrittenParent = tryAnd(parent);

        // try children
        //final Node[] par = rewrittenParent.arrayClone();
        int arity = rewrittenParent.arity();
        for (int i = 0; i < arity; i++) {
            final Node childNode = rewrittenParent.node(i);
            final Node rewriteChild = internalRewrite(childNode);
            if (childNode != rewriteChild) {
                rewrittenParent.setChild(i, rewriteChild);
                this.rewritten = true;
            }
        }

        return rewrittenParent;
    }

    /**
     * Try to rewrite true and true, false and false.
     *
     * @param n The node to attempt to rewrite.
     * @return The rewritten node, or the original node if not rewritten.
     */
    private Node tryAnd(final Node n) {
        //if (parent.getTemplate() == StandardExtensions.EXTENSION_AND) {
        if (n.arity()!=2 || !And.IDENTIFIER.equals(n.id()))
            return n;

        Node x = n.node(0);
        Node y = n.node(1);

        int xp = polarity(x), yp = polarity(y);
        if (xp == +1) /*&& child2.getTemplate() != StandardExtensions.EXTENSION_CONST_SUPPORT)*/ {
            this.rewritten = true;
            return y;
        }
        if (yp == +1) /*&& child1.getTemplate() != StandardExtensions.EXTENSION_CONST_SUPPORT)*/ {
            this.rewritten = true;
            return x;
        }
        if (xp == -1) {
            this.rewritten = true;
            return Literal.False;
        }
        if (yp == -1) {
            this.rewritten = true;
            return Literal.False;
        }
        //}
        return n;
    }
}