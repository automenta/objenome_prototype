/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import objenome.impl.ClassBuilder;
import objenome.impl.ClassBuilder.DependencyKey;
import objenome.impl.MultiClassBuilder;

/**
 * Dependency-injection Context which supports genetic search to evolve build patterns
 */
public class GeneContext extends AbstractProtoContext implements MultiContext {
    
    
        

    /** new random chromosome */
    public Objosome newObjosome() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    @Override
    public MultiClassBuilder usable(Class abstractClass, Scope scope, Class<?>... klasses) {
        return (MultiClassBuilder)usable(abstractClass, scope, 
                new MultiClassBuilder(abstractClass, Lists.newArrayList( klasses ) ));
    }

    protected List<Objene> getParameters(Object[] keys) {
        List<Objene> l = new ArrayList();
        for (Object k : keys) {
            Builder b = getBuilder(k);
            if (b == null) {
                //throw new RuntimeException(this + " does not have Builder for key: " + k);
                
                ClassBuilder cb = getClassBuilder(k.getClass().equals(Class.class) ? (Class)k : k.getClass());
                cb.updateConstructorDependencies();
                for (Object v : cb.getInitValues()) {
                    if (v instanceof DependencyKey)
                        v = ((DependencyKey)v).key;
                    
                    Builder bv = getBuilder(v);
                    if (bv instanceof Parameterized)
                        l.addAll( ((Parameterized)bv).getGenes() );
                }
                
                //TODO handle setters, etc
                
            }
            else if (b instanceof Parameterized) {
                l.addAll( ((Parameterized)b).getGenes() );
            }
        }
        return l;
    }
    
    /** creates a new random objosome,
     *  analogous to Context.get(Object key) except this represents the set of desired
     *  keys for which to evolve a set of Objosomes can be evolved to generate
     */
    public Objosome newObjosome(Object... keys) {
        return new Objosome(getParameters(keys));
    }
    
   
    /** realize the phenotype of a chromosome */
    public Context build(Objosome objsome, Object[] keys) {

        //populate a new DefaultContext as configured by this Objosome and the static parameters provided in GeneContext parent context
        return null;
    }    
}
