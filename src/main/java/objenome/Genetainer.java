/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open of template in of editor.
 */
package objenome;

import objenome.solve.RandomSolver;
import objenome.solve.Solver;
import objenome.problem.Problem;
import objenome.solution.dependency.Builder;
import objenome.solution.dependency.Scope;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import objenome.problem.DecideNumericValue;
import objenome.problem.DecideNumericValue.DecideBooleanValue;
import objenome.problem.DecideNumericValue.DecideDoubleValue;
import objenome.problem.DecideNumericValue.DecideIntegerValue;
import objenome.problem.DevelopMethod;
import objenome.solution.SetBooleanValue;
import objenome.solution.SetDoubleValue;
import objenome.solution.dependency.ClassBuilder;
import objenome.solution.dependency.ClassBuilder.DependencyKey;
import objenome.solution.dependency.DecideImplementationClass;
import objenome.solution.SetImplementationClass;
import objenome.solution.SetIntegerValue;

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
    
    public Genetainer(AbstractPrototainer p) {
        super(p.builders, p.scopes, p.setterDependencies, p.constructorDependencies, p.forConstructMethod);
    }
        
    @Override
    public DecideImplementationClass any(Class abstractClass, Scope scope, Class<?>... klasses) {
        if (abstractClass == null)
            throw new RuntimeException("abstractClass is null");        
        
        return (DecideImplementationClass)usable(abstractClass, scope, 
                new DecideImplementationClass(abstractClass, Lists.newArrayList( klasses ) ));
    }

    public Builder any(Object key, Class<?>[] klasses) {
        Builder b = getBuilder(key);
        if (b instanceof ClassBuilder) {
            return any( ((ClassBuilder)b).type(), klasses );
        }
        return null;
    }
    
    protected List<Problem> getProblems(ClassBuilder cb, List<Object> path, List<Problem> problems) {
        cb.updateConstructorDependencies(false);

        if (cb.getInitValues() == null) {
            /*System.out.println(cb.getInitTypes());
            System.out.println(cb.getInitValues());*/
            throw new RuntimeException(this + " unknown how to Build component: " + path);
        }

        Class c = cb.type();
        for (Method m : c.getMethods()) {
            if (Modifier.isAbstract( m.getModifiers() )) {
                problems.add(new DevelopMethod(m) );
            }
        }
        
        for (Object v : cb.getInitValues()) {
            //System.out.println("  Class Builder Init Value: "+ cb + " " + v);           
            
            
            if (!(v instanceof DependencyKey)) {
                throw new RuntimeException("Unknown init value type: " + v);
            }
            
            List nextPath = new ArrayList(path);
            nextPath.add(v);
            
            Builder bv = getBuilder(((DependencyKey)v).key);
            
            if (bv instanceof DecideImplementationClass) {
                
                problems.add((Problem)bv);
                
                //recurse for each choice
                getProblems(((DecideImplementationClass)bv).implementors, nextPath, problems);
            }
            else {
                //System.out.println("  Class Builder Init Value Builder: "+ cb + " " + bv);
                getProblems(bv, nextPath, problems);
            }
        }
        for (Parameter p : cb.getInitPrimitives()) {
            List nextPath = new ArrayList(path);
            nextPath.add(p);
            
            if (p.getType() == int.class) {
                problems.add(new DecideIntegerValue(p, nextPath, 
                        getIntMinDefault(), getIntMaxDefault()));
            }
            else if (p.getType() == double.class) {
                problems.add(new DecideDoubleValue(p, nextPath, 
                        getDoubleMinDefault(), getDoubleMaxDefault()) );
            }
            else if (p.getType() == boolean.class) {
                problems.add(new DecideBooleanValue(p, nextPath) );    
            }
            else {
                throw new RuntimeException("primitive Parameter " + nextPath + " " + p + " not yet supported");
            }
        }

        //TODO handle setters, etc
        //System.out.println("Class Builder: "+ path + " " + cb);

        return problems;
    }
    
    protected List<Problem> getProblems(Iterable keys, List<Object> parentPath, List<Problem> problems) {
        for (Object k : keys) {
            problems = getProblems(k, parentPath, problems);
        }
        return problems;
    }
    
    protected List<Problem> getProblems(Object k, List<Object> parentPath, List<Problem> problems) {
        if (problems == null) problems = new ArrayList();
            
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
            getProblems(cb, path, problems);
        }
        else {
            
        
            if (b instanceof DecideImplementationClass) {
                DecideImplementationClass mcb = (DecideImplementationClass)b;
                path.add(mcb);

                problems.add(mcb);
                
                return getProblems(mcb.implementors, path, problems);
            }
            else if (b instanceof ClassBuilder) {                      
                if (b.equals(previousPathElement)) {
                    return problems;
                    //throw new RuntimeException("Cyclic dependency: " + path + " -> " + b);
                }

                path.add(b);
                
                if (k!=b.type())
                    getProblems( b.type(), path, problems);
                else
                    getProblems( (ClassBuilder)b, path, problems);
                return problems;
                
            }
            else {
                throw new RuntimeException("decide what this means: Builder=" + b);                
                /*if (b instanceof Parameterized) {
                    genes.addAll( ((Parameterized)b).getGenes(path) );
                } */               
            }
        }
        
        return problems;
        
    }

    
    public List<Object> getKeyClasses() {
        //TODO use setter dependencies also?
        return getConstructorDependencies().stream().map(d -> d.getContainerKey()).filter(k -> k!=null).collect(toList());
    }
    
    /** creates a new random objosome,
  analogous to AbstractContainer.genome(Object key) except this represents of set of desired
  keys for which to evolve a set of Objosomes can be evolved to generate
     */
    @Deprecated public Objenome genome(Object... keys) {
        try {
            return genome((Solver)new RandomSolver(), keys);
        } catch (IncompleteSolutionException ex) {
            throw new RuntimeException(ex.toString(), ex);
        }
    }
    
    public interface Solution {
        public Objene apply(Problem p);
    }
    
    public static class RandomSolution implements Solution {

        @Override
        public Objene apply(Problem p) {
            if (p instanceof DecideNumericValue) {
                if (p instanceof DecideBooleanValue) {
                    return new SetBooleanValue(((DecideBooleanValue)p), Math.random() < 0.5);
                }
                else if (p instanceof DecideIntegerValue) {
                    return new SetIntegerValue(((DecideIntegerValue)p), Math.random());
                }
                else if (p instanceof DecideDoubleValue) {
                    return new SetDoubleValue(((DecideDoubleValue)p), Math.random());
                }
            }
            else if (p instanceof DecideImplementationClass) {
                return new SetImplementationClass(((DecideImplementationClass)p), Math.random());
            }
            return null;
        }
        
        
    }
    
    
    
    
    public static class IncompleteSolutionException extends Exception {

        public IncompleteSolutionException(Iterable<Problem> p, Object[] keys, Genetainer g) {
            super("Missing solution(s) for " + p + " to build " + Arrays.toString(keys) + " in " + g + ": " + g);
        }    
        
    }
    
    public Objenome genome(Solver solver, Object... keys) throws IncompleteSolutionException {
        List<? extends Object> k;
        if (keys.length == 0) {
            //default: use all autowired dependents
            k = getKeyClasses();
        }
        else {
            k = Lists.newArrayList(keys);
        }
        
        List<Problem> p = getProblems(k, null, null);
        
        Map<Problem,Solution> problems = new HashMap();
        for (Problem x : p) {
            problems.put(x, null);
        }
        solver.solve(this, problems);
                
        List<Problem> missing = new ArrayList();
        for (Map.Entry<Problem, Solution> e : problems.entrySet()) {
            if (e.getValue() == null)
                missing.add(e.getKey());
        }
        
        if (!missing.isEmpty())
            throw new IncompleteSolutionException(missing, keys, this);

        
        List<Objene> g = new ArrayList();
        for (Map.Entry<Problem, Solution> e : problems.entrySet()) {
            g.add(e.getValue().apply(e.getKey()));
        }
        
        return new Objenome(this, g);
        
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
