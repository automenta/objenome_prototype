/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * Object chromosome
 */
public class Objosome extends ArrayList<Objene> {
    
    public final Genetainer parentContext;
    
    /** generated context, constructed lazily */
    private Container context = null;

    public Objosome(Genetainer context, List<Objene> parameters) throws InvalidRepresentationException {
        super(parameters);        
        
        this.parentContext = context;
    }
    
    /** gets the generated context of this Objosome with respect to the parent context.
        Parent is a Genetainer but the generated context is a Container
        which functions as an ordinary deterministic dependency injection container.     */
    public Container context() {
        if (context!=null)
            return context;
        
        context = new Phenotainer(this);
        
        
        return context;
    }

    
    public Chromosome newChromosome(final Scoring scoring) {
        return newChromosome(scoring, this);        
    }
    
    
    public AbstractListChromosome<Objene> newChromosome(final Scoring scoring, List<Objene> genes) {
        return new AbstractListChromosome<Objene>(genes) {

            @Override
            protected void checkValidity(List<Objene> genes) throws InvalidRepresentationException {
                String e = Objosome.this.parentContext.getChromosomeError(genes);
                if (e!=null)
                    throw new InvalidRepresentationException(new DummyLocalizable(e));
            }

            @Override
            public double fitness() {
                return scoring.apply(Objosome.this);
            }

            @Override
            public AbstractListChromosome<Objene> newFixedLengthChromosome(List<Objene> list) {
                return newChromosome(scoring, list);
            }
            
        };
    }

    public interface Scoring extends Function<Objosome,Double> {
        
    }
    
}
