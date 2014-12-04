Evolutionary Dependency Injection
------------------------------

A [dependency injection](https://en.wikipedia.org/wiki/Dependency_injection) container that **automatically assembles software** from combinations of components.  Ambiguities in the choice of particular dependencies and parameter constants forms a non-deterministic set of parameters that can be mutated, combined, and optimized to maximize supplied design goals.  And at its core, a **deterministic**, **minimal**, **fluent**, **pure Java**, **no-nonsense** **dependency-injection container**.


![base](https://raw.githubusercontent.com/automenta/objenome/master/objenome.jpg)


Hyperparameter Optimization
---------------------------
[Hyperparameter optimization](https://en.wikipedia.org/wiki/Hyperparameter_optimization) of arbitrary APIs by automatic application of evolutionary, numeric optimization, constraint satisfaction, and other kinds of search, directly to any given set of software components (ex: java classes) through their API.

Solutions to the set of unknown numeric and enumerated parameters involved in a non-deterministic component container (Multitainer) form a plan (genotype) for being able to instantiate desired classes (phenotype) -- even if nothing else is known except the available classes themselves.


How it Works
------------

Objenome is built on a refactored and generalized version of [MentaContainer](http://mentacontainer.soliveirajr.com/mtw/Page/Intro/en/mentacontainer-overview), which provided a minimal and straightforward DI container.

[Apache Commons Math](http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/) provides genetic algorithm, numeric optimization, and other numeric solvers.

An adapted version of the Genetic Programming library [EpochX 2.0](https://github.com/tc33/) is included in the 'evolve' packages.  There are significant architectural differences, including the elimination of all EpochX's need for static classes, and further refactoring to simplify the API. (Currently, this fork does not include EpochX's Context-free-grammar (CFG) packages, though these can be integrated later.)  GP evolution configuration has been modified to use an Objenome dependency-injection container __internally__.  Javassist dynamic bytecode is available to automatically replace unimplemented abstract methods of constructed instances with procedures and expressions evolved to maximize a provided fitness function.


Basic '*Container*' Dependency Injection
======================

**Basic Autowiring with use() & Instantiation**
``` java
Container c = new Container();
c.use(Part.class, Part0.class);
Part p = c.get(Part.class);
```

**Singleton Scope**
``` java
Part x = c.the("part", new Part0());
Part y = c.the("part", new Part0());
//assertTrue(x==y);

Part z = c.the("part2", new Part0());
//assertTrue(x!=z);

Part w = c.the(new Part0());
//assertTrue(w!=z);

Part v = c.the(Part0.class);
//assertTrue(v==w);
```

**Multiple Levels of Dependencies, Mixing use() & usable()**
``` java
Container c = new Container();    
    
/* public static class JdbcUserDAO implements UserDAO 
      public void setConnection(Connection conn)*/
c.usable(JdbcUserDAO.class);
c.usable(ParameterX.class);
c.use(Connection.class); //wires to setter

/* public ServiceNeedingDAOandParameter(UserDAO userDAO, ParameterX x) */
service = c.get(ServiceNeedingDAOandParameter.class);
```

Basic '*Multitainer*' Dependency Injection
====
``` java
Genetainer g = new Genetainer();

g.any(Part.class, of(Part0.class, Part1.class));
                
Objenome o = g.solve(Machine.class);

Machine m = o.get(Machine.class);

Machine maybeDifferent = o.mutate().get(Machine.class); 
```


***More documentation coming soon: additional Unit Test examples EXPLAINED***
-----------------------------------------------------------------

Numeric Optimization
==================

**See NumericAnalysisTest**


Code Evolution
============

**See MethodsGPEvolvedTest and STGP***Test's**

ClassLoader Combinatorics
====

**See PackatainerTest and JartainerTest**
