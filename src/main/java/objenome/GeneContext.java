/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

import com.google.common.collect.Lists;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import objenome.gene.BooleanSelect;
import objenome.gene.DoubleSelect;
import objenome.gene.IntegerSelect;
import objenome.impl.ClassBuilder;
import objenome.impl.ClassBuilder.DependencyKey;
import objenome.impl.MultiClassBuilder;

/**
 * Dependency-injection Context which supports genetic search to evolve build patterns
 */
public class GeneContext extends AbstractProtoContext implements MultiContext {       
    private int intMinDefault = 0;
    private int intMaxDefault = 1;
    private int doubleMinDefault = 0;
    private int doubleMaxDefault = 1;
    
    @Override
    public MultiClassBuilder usable(Class abstractClass, Scope scope, Class<?>... klasses) {
        return (MultiClassBuilder)usable(abstractClass, scope, 
                new MultiClassBuilder(abstractClass, Lists.newArrayList( klasses ) ));
    }

    
    
    protected List<Objene> getParameters(Iterable keys, List<Object> parentPath, List<Objene> genes) {
        if (genes == null) genes = new ArrayList();
        
        
        for (Object k : keys) {
            
            //TODO lazily calculate as needed, not immediately because it may not be used
            List<Object> path;
            if (parentPath == null) path = new ArrayList();
            else path = new ArrayList(parentPath);
            
            path.add(k);
                    
            Builder b = getBuilder(k);
            if (b == null) {
                //throw new RuntimeException(this + " does not have Builder for key: " + k);
                
                ClassBuilder cb = getClassBuilder(k.getClass().equals(Class.class) ? (Class)k : k.getClass());
                cb.updateConstructorDependencies(false);
                
                if (cb.getInitValues() == null) {
                    /*System.out.println(cb.getInitTypes());
                    System.out.println(cb.getInitValues());*/
                    throw new RuntimeException(this + " unknown how to Build component: " + k + " (path=" + path + ")");
                }
                
                for (Object v : cb.getInitValues()) {
                    if (v instanceof DependencyKey)
                        v = ((DependencyKey)v).key;
                    
                    Builder bv = getBuilder(v);
                    if (bv instanceof Parameterized) {
                        genes.addAll( ((Parameterized)bv).getGenes(path) );
                        
                        if (bv instanceof MultiClassBuilder) {
                            //recurse for each choice
                            getParameters(((MultiClassBuilder)bv).implementors, path, genes);
                        }
                    }
                }
                for (Parameter p : cb.getInitPrimitives()) {
                    if (p.getType() == int.class) {
                        genes.add( new IntegerSelect(p, path, 
                                getIntMinDefault(), getIntMaxDefault()) );
                    }
                    else if (p.getType() == double.class) {
                        genes.add( new DoubleSelect(p, path, 
                                getDoubleMinDefault(), getDoubleMaxDefault()) );
                    }
                    else if (p.getType() == boolean.class) {
                        genes.add( new BooleanSelect(p, path) );    
                    }
                    else {
                        throw new RuntimeException("primitive Parameter " + path + " " + p + " not yet supported");
                    }
                }
                
                //TODO handle setters, etc
                
            }
            else if (b instanceof Parameterized) {
                genes.addAll( ((Parameterized)b).getGenes(path) );
            }
        }
        return genes;
    }
    
    /** creates a new random objosome,
     *  analogous to Context.get(Object key) except this represents the set of desired
     *  keys for which to evolve a set of Objosomes can be evolved to generate
     */
    public Objosome get(Object... keys) {
        return new Objosome(getParameters(Lists.newArrayList(keys), null, null));
    }
    
   
    /** realize the phenotype of a chromosome */
    public Context build(Objosome objsome, Object[] keys) {

        //populate a new DefaultContext as configured by this Objosome and the static parameters provided in GeneContext parent context
        return null;
    }    

    /** used if a parameter annotation is not present on primitive parameters */
    public int getIntMinDefault() {  return intMinDefault;    }
    /** used if a parameter annotation is not present on primitive parameters */
    public int getIntMaxDefault() {  return intMaxDefault;    }
    /** used if a parameter annotation is not present on primitive parameters */
    public int getDoubleMinDefault() {  return doubleMinDefault;    }
    /** used if a parameter annotation is not present on primitive parameters */
    public int getDoubleMaxDefault() {  return doubleMaxDefault;    }

    public void setIntMaxDefault(int intMaxDefault) {
        this.intMaxDefault = intMaxDefault;
    }

    public void setIntMinDefault(int intMinDefault) {
        this.intMinDefault = intMinDefault;
    }

    public void setDoubleMaxDefault(int doubleMaxDefault) {
        this.doubleMaxDefault = doubleMaxDefault;
    }

    public void setDoubleMinDefault(int doubleMinDefault) {
        this.doubleMinDefault = doubleMinDefault;
    }

}
