/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.jurls.approximation;

import objenome.op.Scalar;
import objenome.jurls.Sum;
import objenome.jurls.Product;
import objenome.jurls.TanhSigmoid;
import objenome.op.DiffableFunction;
import objenome.jurls.ATanhSigmoid;
import objenome.jurls.Exp;
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
                    ys.add(
                            new Product(newParameter(-1, 1, pi++, parameterList), input)
                    );
                }
                ys.add(newParameter(-1, 1, pi++, parameterList));

                xs.add(
                        new Product(
                                newParameter(-1, 1, pi++, parameterList),
                                new TanhSigmoid(
                                        new Sum(ys.toArray(new DiffableFunction[ys.size()]))
                                )
                        )
                );
            }
            xs.add(newParameter(-1, 1, pi++, parameterList));

            return new Sum(
                    newParameter(-1, 1, pi++, parameterList),
                    new Product(
                            newParameter(-1, 1, pi++, parameterList),
                            new TanhSigmoid(
                                    new Sum(xs.toArray(new DiffableFunction[xs.size()]))
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
                    ys.add(
                            new Product(newParameter(-0.01, 0.01, pi++, parameterList), input)
                    );
                }
                ys.add(newParameter(-1, 1, pi++, parameterList));

                xs.add(
                        new Product(
                                newParameter(-1, 1, pi++, parameterList),
                                new ATanhSigmoid(
                                        new Sum(ys.toArray(new DiffableFunction[ys.size()]))
                                )
                        )
                );
            }
            xs.add(newParameter(-1, 1, pi++, parameterList));

            return new Sum(
                    newParameter(-1, 1, pi++, parameterList),
                    new Product(
                            newParameter(-1, 1, pi++, parameterList),
                            new ATanhSigmoid(
                                    new Sum(xs.toArray(new DiffableFunction[xs.size()]))
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
                    f = new Sum(newParameter(-1, 0, pi++, parameterList), f);
                    f = new Product(newParameter(fact, fact, pi++, parameterList), f);
                    ys.add(f);
                }

                ys.add(newParameter(0, 0, pi++, parameterList));
                DiffableFunction sum = new Sum(ys.toArray(new DiffableFunction[ys.size()]));
                DiffableFunction sqr = new Product(sum, sum);

                Scalar p = newParameter(-1, -1, pi++, parameterList);
                p.setUpperBound(0);

                DiffableFunction product = new Product(p, sqr);
                DiffableFunction exp = new Exp(product);
                DiffableFunction product2 = new Product(
                        newParameter(1, 1, pi++, parameterList),
                        exp
                );
                xs.add(product2);
            }

            xs.add(newParameter(0, 0, pi++, parameterList));

            return new Product(
                    newParameter(1, 1, pi++, parameterList),
                    new Sum(xs.toArray(new DiffableFunction[xs.size()]))
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
                    ys.add(
                            new Product(
                                    newParameter(f, f, pi++, parameterList),
                                    new Sum(
                                            input,
                                            newParameter(-1, 0, pi++, parameterList)
                                    )
                            )
                    );
                }
                ys.add(newParameter(0, 0, pi++, parameterList));
                xs.add(new Product(
                        newParameter(1, 1, pi++, parameterList),
                        new CosineDiffable(
                                new Sum(ys.toArray(new DiffableFunction[ys.size()]))))
                );
            }

            xs.add(newParameter(0, 0, pi++, parameterList));
            return new Sum(
                    newParameter(0, 0, pi++, parameterList),
                    new Product(
                            newParameter(1, 1, pi++, parameterList),
                            new Sum(xs.toArray(new DiffableFunction[xs.size()]))
                    )
            );
        };
    }
}
