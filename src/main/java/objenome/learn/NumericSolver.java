/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.learn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import objenome.Objene;
import objenome.Objenome;
import objenome.gene.Numeric;

/**
 *
 * @author me
 */
public abstract class NumericSolver<C>  {
    public final Objenome objenome;
    public final List<Numeric> variables = new ArrayList();
    public final Function<C, Double> function;
    public final Class<? extends C> model;

    public NumericSolver(Objenome o, Class<? extends C> model, Function<C, Double> function) {
        this.model = model;
        this.function = function;
        this.objenome = o;
        
        for (Objene g : o.getGeneList()) {
            if (!(g instanceof Numeric)) {
                throw new RuntimeException(this + " only applicable if " + o + " has only Numeric genes; " + g + " is not Numeric");
            }
            variables.add((Numeric) g);
        }
        if (variables.isEmpty()) {
            throw new RuntimeException(o + " has no numeric variables to solve");
        }
    }

    protected double eval() {
        //objenome.commit();
        return function.apply(objenome.get(model));
    }
    
    
    /** var = gene, from different casts */
    public double getMin(Numeric var, Objene gene) {
        return var.getMin().doubleValue();
    }
    
    /** var = gene, from different casts */
    public double getMax(Numeric var, Objene gene) {
        return var.getMax().doubleValue();
    }    
}
