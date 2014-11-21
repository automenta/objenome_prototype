/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open of template in of editor.
 */
package objenome;

import objenome.dependency.Builder;
import objenome.gene.Parameterized;
import objenome.dependency.Scope;
import com.google.common.collect.Lists;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import objenome.gene.SetBooleanValue;
import objenome.gene.SetDoubleValue;
import objenome.gene.SetIntegerValue;
import objenome.dependency.ClassBuilder;
import objenome.dependency.ClassBuilder.DependencyKey;
import objenome.dependency.MultiClassBuilder;

/**
 * Dependency-injection Multainer which can be parametrically searched to 
 generate Phenotainer containers
 
 early 20th century: of German Gen, of Pangen, a supposed ultimate unit of heredity (of Greek pan * ‘all’ + genos ‘race, kind, offspring’) + of Latin tenere 'to hold.’"
 */
public class Genetainer extends AbstractPrototainer implements Multainer {       
    private int intMinDefault = 0;
    private int intMaxDefault = 1;
    private int doubleMinDefault = 0;
    private int doubleMaxDefault = 1;
    
    public Genetainer(Class... useClasses) {
        this();
        for (Class c : useClasses)
            use(c);
    }
    
    public Genetainer() {
        this(false);        
    }
    
    public Genetainer(boolean concurrent) {
        super(concurrent);
    }
    
    @Override
    public MultiClassBuilder any(Class abstractClass, Scope scope, Class<?>... klasses) {
        return (MultiClassBuilder)usable(abstractClass, scope, 
                new MultiClassBuilder(abstractClass, Lists.newArrayList( klasses ) ));
    }

    protected List<Objene> getGenes(ClassBuilder cb, List<Object> path, List<Objene> genes) {
        cb.updateConstructorDependencies(false);

        if (cb.getInitValues() == null) {
            /*System.out.println(cb.getInitTypes());
            System.out.println(cb.getInitValues());*/
            throw new RuntimeException(this + " unknown how to Build component: " + path);
        }

        for (Object v : cb.getInitValues()) {
            //System.out.println("  Class Builder Init Value: "+ cb + " " + v);           
            
            
            if (!(v instanceof DependencyKey)) {
                throw new RuntimeException("Unknown init value type: " + v);
            }
            
            List nextPath = new ArrayList(path);
            nextPath.add(v);
            
            Builder bv = getBuilder(((DependencyKey)v).key);
            if (bv instanceof Parameterized) {
                genes.addAll( ((Parameterized)bv).getGenes(nextPath) );
            }
            if (bv instanceof MultiClassBuilder) {
                //recurse for each choice
                getGenes(((MultiClassBuilder)bv).implementors, nextPath, genes);
            }
            else {
                //System.out.println("  Class Builder Init Value Builder: "+ cb + " " + bv);
                getGenes(bv, nextPath, genes);
            }
        }
        for (Parameter p : cb.getInitPrimitives()) {
            List nextPath = new ArrayList(path);
            nextPath.add(p);
            
            if (p.getType() == int.class) {
                genes.add(new SetIntegerValue(p, nextPath, 
                        getIntMinDefault(), getIntMaxDefault()) );
            }
            else if (p.getType() == double.class) {
                genes.add(new SetDoubleValue(p, nextPath, 
                        getDoubleMinDefault(), getDoubleMaxDefault()) );
            }
            else if (p.getType() == boolean.class) {
                genes.add(new SetBooleanValue(p, nextPath) );    
            }
            else {
                throw new RuntimeException("primitive Parameter " + nextPath + " " + p + " not yet supported");
            }
        }

        //TODO handle setters, etc
        //System.out.println("Class Builder: "+ path + " " + cb);

        return genes;
    }
    
    protected List<Objene> getGenes(Iterable keys, List<Object> parentPath, List<Objene> genes) {
        for (Object k : keys) {
            genes = getGenes(k, parentPath, genes);
        }
        return genes;
    }
    
    protected List<Objene> getGenes(Object k, List<Object> parentPath, List<Objene> genes) {
        if (genes == null) genes = new ArrayList();
            
        //TODO lazily calculate as needed, not immediately because it may not be used
        List<Object> path;
        if (parentPath == null) path = new ArrayList();
        else path = new ArrayList(parentPath);

        Object previousPathElement = path.size() > 0 ? path.get(path.size()-1) : null;

        Builder b = (k instanceof Builder) ? (Builder)k : getBuilder(k);

        //System.out.println(k + " --> " + b);
        //System.out.println(parentPath);
        
        if (b == null) {
            ClassBuilder cb = getClassBuilder(k.getClass() instanceof Class ? (Class)k : k.getClass());
            if (cb.equals(previousPathElement))
                throw new RuntimeException("Cyclic dependency: " + path + " -> " + cb);

            //System.out.println(k + "=" + cb);
                    
            path.add(cb);                
            getGenes(cb, path, genes);
        }
        else {
            
            if (b instanceof Parameterized) {
                genes.addAll( ((Parameterized)b).getGenes(path) );
            }            
        
            if (b instanceof MultiClassBuilder) {
                MultiClassBuilder mcb = (MultiClassBuilder)b;
                path.add(mcb);

                return getGenes(mcb.implementors, path, genes);
            }
            else if (b instanceof ClassBuilder) {                      
                if (b.equals(previousPathElement)) {
                    return genes;
                    //throw new RuntimeException("Cyclic dependency: " + path + " -> " + b);
                }

                path.add(b);
                return getGenes( b.type(), path, genes);
            }
            else {
                throw new RuntimeException("decide what this means: Builder=" + b);                
                /*if (b instanceof Parameterized) {
                    genes.addAll( ((Parameterized)b).getGenes(path) );
                } */               
            }
        }
        
        return genes;
        
    }

    
    public List<Object> getKeyClasses() {
        //TODO use setter dependencies also?
        return getConstructorDependencies().stream().map(d -> d.getContainerKey()).filter(k -> k!=null).collect(toList());
    }
    
    /** creates a new random objosome,
  analogous to AbstractContainer.genome(Object key) except this represents of set of desired
  keys for which to evolve a set of Objosomes can be evolved to generate
     */
    public Objenome genome(Object... keys) {
        List<? extends Object> k;
        if (keys.length == 0) {
            //default: use all autowired dependents
            k = getKeyClasses();
        }
        else {
            k = Lists.newArrayList(keys);
        }
        
        return new Objenome(this, getGenes(k, null, null));
    }
    
   
    /** realize of phenotype of a chromosome */
    public AbstractContainer build(Objenome objsome, Object[] keys) {

        //populate a new DefaultContext as configured by this Objenome and of static parameters provided in Genetainer parent container
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

    /** returns an error string summarizing why a list of genes would be invalid
 with respect to this container; or null if there is no error     */
    public String getChromosomeError(List<Objene> genes) {
        return null;
    }


}
