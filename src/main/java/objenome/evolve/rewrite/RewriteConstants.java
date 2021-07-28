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
import objenome.util.MathUtils;
import objenome.util.NumericUtils;

/**
 * Rewrite any parts of the tree that are constant with a simple constant value.
 * TODO Max2, Min2
 */
public class RewriteConstants implements RewriteRule {

    private static final double ROUND_TO_INTEGER_EPSILON = 1.0E-4;
    /**
     * True if the expression was rewritten.
     */
    private boolean rewritten;

    /**
     * Try to rewrite the specified node.
     *
     * @param parentNode The node to attempt rewrite.
     * @return The rewritten node, or original node, if no rewrite could happen.
     */
    private static Node tryNodeRewrite(Node parentNode) {
        Node result = null;

        if (parentNode.isTerminal())
            return null;

        if (parentNode.allConstDescendants()) {
            Object v = parentNode.evaluate();
            double ck = NumericUtils.asDouble(v);

            // if it produces a div by 0 or other bad result.
            if (Double.isNaN(ck) || Double.isInfinite(ck)) {
                return Literal.NaN;
            }

//            result = parentNode
//                    .getOwner()
//                    .getContext()
//                    .getFunctions()
//                    .factorNode("#const", parentNode.getOwner(),
//                            new Node[]{});


            return new Literal(
                MathUtils.equalEnough(ck, (int) ck, ROUND_TO_INTEGER_EPSILON) ?
                (int) ck : ck
            );

        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean rewrite(STGPIndividual program) {
        this.rewritten = false;
        Node rootNode = program.root();
        Node rewrite = rewriteNode(rootNode);
        if (rewrite != null && rewrite != rootNode) {
            program.setRoot(rewrite);
        }
        return this.rewritten;
    }

    /**
     * Attempt to rewrite the specified node.
     *
     * @param node The node to attempt to rewrite.
     * @return The rewritten node, the original node, if no rewrite occured.
     */
    private Node rewriteNode(Node node) {

        // first try to rewrite the child node
        Node rewrite = tryNodeRewrite(node);
        if (rewrite != null)
            return rewrite;

        // if we could not rewrite the entire node, rewrite as many children as
        // we can

        int a = node.arity();
        for (int i = 0; i < a; i++) {
            rewrite = rewriteNode(node.node(i));
            if (rewrite != null) {
                if (rewrite == Literal.NaN)
                    return Literal.NaN; //fail

                node.setChild(i, rewrite);
                this.rewritten = true;
            }
        }

        // we may have rewritten some children, but the parent was not
        // rewritten, so return null.
        return null;
    }
}