/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.util;

import java.lang.reflect.Array;
import java.util.Collection;
import objenome.op.DiffableFunction;
import objenome.op.Scalar;
import objenome.jurls.approximation.DiffableFunctionMarshaller;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author thorsten
 */
public class Utils {

    public static <T> T[] toArray(final Collection c) {
        return (T[]) c.toArray((T[]) Array.newInstance(c.iterator().next().getClass(), c.size()));
    }

    public static double[] join(double[] state, double action) {
        double[] xs = new double[state.length + 1];
        for (int i = 0; i < state.length; ++i) {
            xs[i] = state[i];
        }
        xs[xs.length - 1] = action;
        return xs;
    }

    public static double q(DiffableFunctionMarshaller fm, double[] state, double action) {
        fm.setInputs(join(state, action));
        return fm.getF().value();
    }

    public static double[] v(DiffableFunctionMarshaller fm, double[] state, double lower, double upper, int num) {
        double max = -Double.MAX_VALUE;
        double maxa = 0;

        for (int i = 0; i < num; ++i) {
            double a = (double) i * (upper - lower) / (double) (num - 1) + lower;
            double _q = q(fm, state, a);
            if (_q > max) {
                max = _q;
                maxa = a;
            }
        }

        return new double[]{maxa, max};
    }

    public static ArrayRealVector gradient(DiffableFunctionMarshaller fm, double[] state, double action, ArrayRealVector result) {
        fm.setInputs(join(state, action));
        return gradient(fm.getF(), fm.getParameters(), result);
    }

    public static ArrayRealVector gradient(DiffableFunction f, Scalar[] parameters, ArrayRealVector result) {
        if (result == null)
            result = new ArrayRealVector(parameters.length);
        
        double d[] = result.getDataRef();
        for (int i = 0; i < parameters.length; ++i) {
            d[i] = f.partialDerive(parameters[i]);
        }
        return result;
    }

    public static double lengthSquare(double[] v) {
        double s = 0;
        for (double x : v) {
            s += x * x;
        }
        return s;
    }
    
    public static double length(double[] v) {
        return Math.sqrt(lengthSquare(v));
    }

    public static double[] add(double[] a, double[] b) {
        assert a.length == b.length;

        double[] v = new double[a.length];

        for (int i = 0; i < a.length; ++i) {
            v[i] = a[i] + b[i];
        }

        return v;
    }

    public static double[] sub(double[] a, double[] b) {
        assert a.length == b.length;

        double[] v = new double[a.length];

        for (int i = 0; i < a.length; ++i) {
            v[i] = a[i] - b[i];
        }

        return v;
    }

    public static double[] mult(double a, double[] b) {
        double[] v = new double[b.length];

        for (int i = 0; i < b.length; ++i) {
            v[i] = a * b[i];
        }

        return v;
    }

    public static double[] normalize(double[] a) {
        double l = length(a);
        if (l < 0.1) {
            l = 0.1;
        }
        double[] v = new double[a.length];

        for (int i = 0; i < a.length; ++i) {
            v[i] = a[i] / l;
        }

        return v;
    }

}
