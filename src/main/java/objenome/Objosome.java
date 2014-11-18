/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import java.util.List;
import java.util.function.Function;
import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 *
 * @author me
 */
public class Objosome extends AbstractListChromosome<Objene> {
    private Scoring scoring;

    public interface Scoring extends Function<Objosome,Double> {
        
    }
    
    
    /** realize the phenotype of a chromosome as a mapping of the specified object keys to instances */
    public ProtoContext build(Chromosome c) {
        //populate new DefaultContext
        return null;
    }
    
    public Objosome(List<Objene> representation) throws InvalidRepresentationException {
        super(representation);
    }

    public Objosome(Scoring scoring) throws InvalidRepresentationException {
        this((List)null);
        this.scoring = scoring;
    }
    
    

    
    @Override
    protected void checkValidity(List<Objene> list) throws InvalidRepresentationException {
        return;
    }

    @Override
    public AbstractListChromosome<Objene> newFixedLengthChromosome(List<Objene> list) {
        return null;
    }

    @Override
    public double fitness() {
        return scoring.apply(this);
    }
    
}
