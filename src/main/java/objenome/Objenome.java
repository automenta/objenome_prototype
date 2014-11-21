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
    private Phenotainer pheno = null;

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
        if (pheno!=null)
            return pheno;
        
        return new Phenotainer(this);
    }

    /** call after genes have changed to update the container */
    public Phenotainer commit() {
        return container().commit();
    }
    
    public <T> T get(Object key) {
        return container().get(key);
    }
    
    /** list of genes, sorted by key */
    public List<Objene> getGeneList() {
        List<Objene> l = new ArrayList(genes.size());
        for (String s : genes.keySet()) {
            Objene g = genes.get(s);
            l.add(g);
        }
        return l;
    }
    

    /** mutates this genome's genes, and commits changes to apply to next generated object */
    void mutate(/* .... mutation opcodes ... */) {
        for ( Objene g : genes.values()) {
            g.mutate();                
        }
        commit();
    }

    /** fitness function */
    public interface Scoring extends Function<Objenome,Double> {
        
    }
    
}
