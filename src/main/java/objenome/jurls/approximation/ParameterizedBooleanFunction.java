/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.jurls.approximation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author thorsten
 */
public class ParameterizedBooleanFunction implements ParameterizedFunction {

    private final int numInputBits;
    private final int numOutputBits;
    private final int[][] cnf;
    private final long[] parameters;
    private final long[] variables;
    private final long[] intermediates;
    private final int[] numBitsPerVariable;
    private final double[] minInputs;
    private final double[] maxInputs;
    private double minOutput = 0;
    private double maxOutput = 0;
    private final Random random = new Random();

    public ParameterizedBooleanFunction(int numInputBits, int numOutputBits, int numInputs) {
        this.numInputBits = numInputBits;
        this.numOutputBits = numOutputBits;
        numBitsPerVariable = new int[numInputs];
        variables = new long[numInputBits];

        int error = 0;
        int sum = 0;
        for (int i = 0; i < numBitsPerVariable.length; ++i) {
            numBitsPerVariable[i] = 0;
            while (error < numInputBits && sum < numInputBits) {
                numBitsPerVariable[i]++;
                sum++;
                error += numInputs;
            }
        }

        ArrayList<List<Integer>> cnf2 = new ArrayList<>();

        for (int i = 1; i <= numInputBits; ++i) {
            for (int j = i + 1; j <= numInputBits; ++j) {
                for (int k = j + 1; k <= numInputBits; ++k) {
                    for (int l = k + 1; l <= numInputBits; ++l) {
                        cnf2.add(Arrays.asList(l, i, j, k));
                        cnf2.add(Arrays.asList(l, i, j, -k));
                        cnf2.add(Arrays.asList(l, i, -j, k));
                        cnf2.add(Arrays.asList(l, i, -j, -k));
                        cnf2.add(Arrays.asList(l, -i, j, k));
                        cnf2.add(Arrays.asList(l, -i, j, -k));
                        cnf2.add(Arrays.asList(l, -i, -j, k));
                        cnf2.add(Arrays.asList(l, -i, -j, -k));
                        cnf2.add(Arrays.asList(-l, i, j, k));
                        cnf2.add(Arrays.asList(-l, i, j, -k));
                        cnf2.add(Arrays.asList(-l, i, -j, k));
                        cnf2.add(Arrays.asList(-l, i, -j, -k));
                        cnf2.add(Arrays.asList(-l, -i, j, k));
                        cnf2.add(Arrays.asList(-l, -i, j, -k));
                        cnf2.add(Arrays.asList(-l, -i, -j, k));
                        cnf2.add(Arrays.asList(-l, -i, -j, -k));
                    }
                }
            }
        }

        cnf = new int[cnf2.size()][];
        for (int i = 0; i < cnf.length; ++i) {
            List<Integer> maxTerm = cnf2.get(i);
            cnf[i] = new int[maxTerm.size()];
            for (int j = 0; j < maxTerm.size(); ++j) {
                cnf[i][j] = maxTerm.get(j);
            }
        }

        parameters = new long[cnf.length];
        Arrays.fill(parameters, ~0l);
        intermediates = new long[cnf.length];

        minInputs = new double[numInputs];
        maxInputs = new double[numInputs];
    }

    private long compute(int clauseIndex) {
        long b = 0;
        int[] maxTerm = cnf[clauseIndex];

        for (int i = 0; i < maxTerm.length; ++i) {
            int literal = maxTerm[i];
            if (literal > 0) {
                b |= variables[literal - 1];
            } else {
                b |= ~variables[-literal - 1];
            }
        }
        b |= parameters[clauseIndex];

        return b;
    }

    private long compute() {
        long a = ~0l;

        for (int j = 0; j < cnf.length; ++j) {
            long b = compute(j);
            intermediates[j] = b;
            a &= b;
        }

        a &= (1l << numOutputBits) - 1l;
        return a;
    }

    @Override
    public double compute(double[] xs) {
        int j = 0;

        for (int i = 0; i < xs.length; ++i) {
            if (xs[i] > maxInputs[i]) {
                maxInputs[i] = xs[i];
            }
            if (xs[i] < minInputs[i]) {
                minInputs[i] = xs[i];
            }
            if (minInputs[i] == maxInputs[i]) {
                maxInputs[i] = minInputs[i] + 0.1;
            }

            double x = (xs[i] - minInputs[i]) / (maxInputs[i] - minInputs[i]);
            long v = Math.round(((1l << numBitsPerVariable[i]) - 1) * x);

            for (int k = 0; k < numBitsPerVariable[i]; ++k, ++j) {
                if (((v >> k) & 1) == 1) {
                    variables[j] = ~0l;
                } else {
                    variables[j] = 0l;
                }
            }
        }

        double x = (double) compute() / (double) ((1l << numOutputBits) - 1);
        return x * (maxOutput - minOutput) + minOutput;
    }

    @Override
    public void oneStepTowards(double[] xs, double y) {
        if (y > maxOutput) {
            maxOutput = y;
        }
        if (y < minOutput) {
            minOutput = y;
        }
        if (minOutput == maxOutput) {
            maxOutput = minOutput + 0.1;
        }

        double a = (compute(xs) - minOutput) / (maxOutput - minOutput);
        double b = (y - minOutput) / (maxOutput - minOutput);
        long currents = Math.round(a * ((1l << numOutputBits) - 1));
        long targets = Math.round(b * ((1l << numOutputBits) - 1));

        for (int i = 0; i < numOutputBits; ++i) {
            boolean target = ((targets >> i) & 1) == 1;
            boolean current = ((currents >> i) & 1) == 1;
            final ArrayList<Integer> ps = new ArrayList<>();
            if (target && !current) {
                for (int j = 0; j < intermediates.length; ++j) {
                    if (((intermediates[j] >> i) & 1) == 0) {
                        ps.add(j);
                    }
                }
            } else if (!target && current) {
                for (int j = 0; j < parameters.length; ++j) {
                    parameters[j] ^= 1l << i;
                    if (((compute(j) >> i) & 1) == 0) {
                        ps.add(j);
                    }
                    parameters[j] ^= 1l << i;
                }
            }

            // ps is the "LOGIC GRADIENT" (my invention)
            if (ps.size() > 0) {
                int p = ps.get(random.nextInt(ps.size()));
                parameters[p] ^= 1l << i;
            }
        }
    }

    @Override
    public int getNumberOfParameters() {
        return parameters.length * numOutputBits;
    }
}
