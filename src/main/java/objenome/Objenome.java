/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * Object Genome
 */
public class Objenome {
    
    Map<String, Objene> genes = new TreeMap();
    
    public final Genetainer parentContext;
    
    /** generated container, constructed lazily
        TODO different construction policies other than caching a single Phenotainer
        instance in this instance
        */
    private Phenotainer context = null;

    public Objenome(Genetainer context, Collection<Objene> parameters) throws InvalidRepresentationException {
        super();
                
        for (Objene o : parameters)
            genes.put(o.key(), o);
        
        this.parentContext = context;
    }
    
    public int size() { return genes.size(); }
    /** gets the generated container of this Objenome with respect to the parent container.
        Parent is a Genetainer but the generated container is a Container
        which functions as an ordinary deterministic dependency injection container.     */
    public Phenotainer container() {
        if (context!=null)
            return context;
        
        context = new Phenotainer(this);
        
        
        return context;
    }

    /** call when if genes have changed */
    public void commit() {
        container().commit();
    }
    
    public <T> T get(Object key) {        
        return container().get(key);
    }
    
    /** list of genes, sorted by key */
    public List<Objene> getGeneList() {
        List<Objene> l = new ArrayList(genes.size());
        for (String s : genes.keySet()) {
            l.add(genes.get(s));
        }
        return l;
    }
    
    public Chromosome newChromosome(final Scoring scoring) {
        return newChromosome(scoring, getGeneList());        
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

    //WARNING this may not be in order, will need to check if test if this provides ordered or not
    public Iterable<Objene> getGenes() {
        return genes.values();
    }

    public interface Scoring extends Function<Objenome,Double> {
        
    }
    
}
