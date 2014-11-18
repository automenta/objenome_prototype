/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import java.util.List;
import java.util.function.Function;
import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * Object chromosome
 */
public class Objosome extends AbstractListChromosome<Objene> {
    private Scoring scoring;



    public interface Scoring extends Function<Objosome,Double> {
        
    }
    

    public Objosome(List<Objene> parameters) throws InvalidRepresentationException {
        super(parameters);        
        
        //this.scoring = scoring;
    }
    
    

    
    @Override
    protected void checkValidity(List<Objene> list) throws InvalidRepresentationException {
    }

    @Override
    public AbstractListChromosome<Objene> newFixedLengthChromosome(List<Objene> list) {
        return null;
    }

    @Override
    public double fitness() {
        //return scoring.apply(this);
        return 0;
    }

    public Objene get(final int i) {
        return getRepresentation().get(i);
    }
    
    
}
