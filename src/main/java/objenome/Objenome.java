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
 * Object Genome
 */
public class Objenome extends ArrayList<Objene> {
    
    public final Genetainer parentContext;
    
    /** generated container, constructed lazily */
    private Container context = null;

    public Objenome(Genetainer context, List<Objene> parameters) throws InvalidRepresentationException {
        super(parameters);        
        
        this.parentContext = context;
    }
    
    /** gets the generated container of this Objenome with respect to the parent container.
        Parent is a Genetainer but the generated container is a Container
        which functions as an ordinary deterministic dependency injection container.     */
    public Container container() {
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
                String e = Objenome.this.parentContext.getChromosomeError(genes);
                if (e!=null)
                    throw new InvalidRepresentationException(new DummyLocalizable(e));
            }

            @Override
            public double fitness() {
                return scoring.apply(Objenome.this);
            }

            @Override
            public AbstractListChromosome<Objene> newFixedLengthChromosome(List<Objene> list) {
                return newChromosome(scoring, list);
            }
            
        };
    }

    public interface Scoring extends Function<Objenome,Double> {
        
    }
    
}