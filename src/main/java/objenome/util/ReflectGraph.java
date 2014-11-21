/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import java.util.HashSet;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import objenome.dependency.Builder;
import objenome.Container;
import objenome.Genetainer;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ConfigurationBuilder;

/**
 * Type graph analsys
 */
public class ReflectGraph  {
    
    public final Set<Class> classes;
    private final Set<String> classNames;
    private final ConfigurationBuilder configuration;

    /** all classes are available */
    public ReflectGraph(Class... classes) {
        this(new String[] { "" }, classes);
    }
    
    /** filters results to contain only what is in a given set of packages */
    public ReflectGraph(String[] packages, Class... classes) {
        this(new ConfigurationBuilder()
            .forPackages(packages)
            //.filterInputsBy(theClasses)
            //.setUrls(ClasspathHelper.forPackage("my.project.prefix"))
            .setScanners(new TypeElementsScanner().publicOnly().includeAnnotations(false).includeFields(false).includeMethods(false), new SubTypesScanner()), classes);
    }
    
    
    public ReflectGraph(ConfigurationBuilder cb, Class... classes) {
        super();
        this.configuration = cb;
        this.classes = Sets.newHashSet(classes );
        this.classNames = this.classes.stream().map(c -> c.getName()).collect(toSet());
    }
    
    /*final Predicate<String> theClasses = new Predicate<String>() {
        @Override public boolean apply(final String t) {
            return classNames.contains(t);
        }
    };*/
    
    public SetMultimap<Class, Class> getAncestorImplementations() {        
        
        HashMultimap<Class, Class> r = HashMultimap.create();
        
        //1. get common ancestors
        //2. map each ancestor to implementations
        Set<Class> superTypes = new HashSet<>();
        for (Class c : classes) {
            for (Class supertype : ReflectionUtils.getAllSuperTypes(c)) {
                superTypes.add(supertype);
            }
        }
        
        Reflections s = new Reflections(configuration);
        
        for (Class c : superTypes) {
            r.putAll(c, s.getSubTypesOf(c));
        }
        
        return r;
    }

    
    public static Set<ClassPath.ClassInfo> getPackageClasses(String packege) throws Exception {
        //https://code.google.com/p/guava-libraries/wiki/ReflectionExplained#ClassPath
        ClassPath classpath = ClassPath.from(ReflectGraph.class.getClassLoader()); 

        return classpath.getTopLevelClasses(packege);
    }
    
//    public static void main(String[] args) {
//        SetMultimap<Class, Class> anc = new ReflectGraph(
//                Genetainer.class, Container.class, Builder.class)                
//                .getAncestorImplementations();
//
//        System.out.println(anc);
//                
//    }
}
