/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.solution.approximation;

import objenome.op.Scalar;
import objenome.op.math.AddNDiff;
import objenome.op.math.MultiplyDiff;
import objenome.op.activate.TanhSigmoid;
import objenome.op.DiffableFunction;
import objenome.op.activate.ATanhSigmoid;
import objenome.op.math.ExpDiff;
import java.util.ArrayList;
import java.util.List;
import objenome.op.trig.CosineDiffable;

/**
 *
 * @author thorsten
 */
public class Generator {

    public static Scalar newParameter(double lower, double upper, int name, List<Scalar> parameterList) {
        Scalar s = new Scalar(Math.random() * (upper - lower) + lower, "p" + name);
        parameterList.add(s);
        return s;
    }

    public static DiffableFunctionGenerator generateTanhFFNN() {
        return (Scalar[] inputs, List<Scalar> parameterList, int numFeatures) -> {
            List<DiffableFunction> xs = new ArrayList<>();
            int pi = 0;

            for (int i = 0; i < numFeatures; ++i) {
                List<DiffableFunction> ys = new ArrayList<>();

                for (final Scalar input : inputs) {
                    ys.add(new MultiplyDiff(newParameter(-1, 1, pi++, parameterList), input)
                    );
                }
                ys.add(newParameter(-1, 1, pi++, parameterList));

                xs.add(new MultiplyDiff(
                                newParameter(-1, 1, pi++, parameterList),
                                new TanhSigmoid(
                                        new AddNDiff(ys.toArray(new DiffableFunction[ys.size()]))
                                )
                        )
                );
            }
            xs.add(newParameter(-1, 1, pi++, parameterList));

            return new AddNDiff(
                    newParameter(-1, 1, pi++, parameterList),
                    new MultiplyDiff(
                            newParameter(-1, 1, pi++, parameterList),
                            new TanhSigmoid(
                                    new AddNDiff(xs.toArray(new DiffableFunction[xs.size()]))
                            )
                    )
            );
        };
    }

    public static DiffableFunctionGenerator generateATanFFNN() {
        return (Scalar[] inputs, List<Scalar> parameterList, int numFeatures) -> {
            List<DiffableFunction> xs = new ArrayList<>();
            int pi = 0;

            for (int i = 0; i < numFeatures; ++i) {
                List<DiffableFunction> ys = new ArrayList<>();

                for (final Scalar input : inputs) {
                    ys.add(new MultiplyDiff(newParameter(-0.01, 0.01, pi++, parameterList), input)
                    );
                }
                ys.add(newParameter(-1, 1, pi++, parameterList));

                xs.add(new MultiplyDiff(
                                newParameter(-1, 1, pi++, parameterList),
                                new ATanhSigmoid(
                                        new AddNDiff(ys.toArray(new DiffableFunction[ys.size()]))
                                )
                        )
                );
            }
            xs.add(newParameter(-1, 1, pi++, parameterList));

            return new AddNDiff(
                    newParameter(-1, 1, pi++, parameterList),
                    new MultiplyDiff(
                            newParameter(-1, 1, pi++, parameterList),
                            new ATanhSigmoid(
                                    new AddNDiff(xs.toArray(new DiffableFunction[xs.size()]))
                            )
                    )
            );
        };
    }

    public static DiffableFunctionGenerator generateRBFNet() {
        return (Scalar[] inputs, List<Scalar> parameterList, int numFeatures) -> {
            List<DiffableFunction> xs = new ArrayList<>();
            int pi = 0;
            final double fact = 2 * numFeatures;

            for (int i = 0; i < numFeatures; ++i) {
                List<DiffableFunction> ys = new ArrayList<>();

                for (final Scalar input : inputs) {
                    DiffableFunction f = input;
                    f = new AddNDiff(newParameter(-1, 0, pi++, parameterList), f);
                    f = new MultiplyDiff(newParameter(fact, fact, pi++, parameterList), f);
                    ys.add(f);
                }

                ys.add(newParameter(0, 0, pi++, parameterList));
                DiffableFunction sum = new AddNDiff(ys.toArray(new DiffableFunction[ys.size()]));
                DiffableFunction sqr = new MultiplyDiff(sum, sum);

                Scalar p = newParameter(-1, -1, pi++, parameterList);
                p.setUpperBound(0);

                MultiplyDiff product = new MultiplyDiff(p, sqr);
                ExpDiff exp = new ExpDiff(product);
                DiffableFunction product2 = new MultiplyDiff(
                        newParameter(1, 1, pi++, parameterList),
                        exp
                );
                xs.add(product2);
            }

            xs.add(newParameter(0, 0, pi++, parameterList));

            return new MultiplyDiff(
                    newParameter(1, 1, pi++, parameterList),
                    new AddNDiff(xs.toArray(new DiffableFunction[xs.size()]))
            );
        };
    }

    public static DiffableFunctionGenerator generateFourierBasis() {
        return (Scalar[] inputs, List<Scalar> parameterList, int numFeatures) -> {
            List<DiffableFunction> xs = new ArrayList<>();
            int pi = 0;

            for (int i = 0; i < numFeatures; ++i) {
                List<DiffableFunction> ys = new ArrayList<>();
                final double f = Math.PI * (i + 1);

                for (final Scalar input : inputs) {
                    ys.add(new MultiplyDiff(
                                    newParameter(f, f, pi++, parameterList),
                                    new AddNDiff(
                                            input,
                                            newParameter(-1, 0, pi++, parameterList)
                                    )
                            )
                    );
                }
                ys.add(newParameter(0, 0, pi++, parameterList));
                xs.add(new MultiplyDiff(
                        newParameter(1, 1, pi++, parameterList),
                        new CosineDiffable(
                                new AddNDiff(ys.toArray(new DiffableFunction[ys.size()]))))
                );
            }

            xs.add(newParameter(0, 0, pi++, parameterList));
            return new AddNDiff(
                    newParameter(0, 0, pi++, parameterList),
                    new MultiplyDiff(
                            newParameter(1, 1, pi++, parameterList),
                            new AddNDiff(xs.toArray(new DiffableFunction[xs.size()]))
                    )
            );
        };
    }
}
