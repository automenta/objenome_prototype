Evolutionary Dependency Injection
------------------------------

A [dependency injection](https://en.wikipedia.org/wiki/Dependency_injection) container that **automatically assembles software** from combinations of components.  Ambiguities in the choice of particular dependencies form a set of combinatorial parameters that can be optimized to maximize a goal function.  It can also be used in the basic deterministic mode as a **minimal**, **fluent**, **pure Java**, **no-nonsense** **dependency injection library**.


![base](https://raw.githubusercontent.com/automenta/objenome/master/objenome.jpg)


Hyperparameter Optimization
---------------------------
This allows [hyperparameter optimization](https://en.wikipedia.org/wiki/Hyperparameter_optimization) of arbitrary APIs by automatic application of evolutionary, numeric optimization, constraint satisfaction, and other kinds of search, directly to any given set of software components (ex: java classes) through their API.

Solutions to the set of unknown numeric and enumerated parameters involved in a non-deterministic component container (Multitainer) form a plan (genotype) for being able to instantiate desired classes (phenotype) -- even if nothing else is known except the available classes themselves.


Powered By..
------------

At its core, Objenome contains a refactored and generalized version of [MentaContainer](http://mentacontainer.soliveirajr.com/mtw/Page/Intro/en/mentacontainer-overview), which provided a minimal and straightforward DI container.

[Apache Commons Math - Genetic](http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/genetics/package-summary.html) package.

An adapted version of the Genetic Programming library [EpochX 2.0](https://github.com/tc33/) is included in the 'evolve' packages.  There are significant architectural differences including the elimination of all static classes and refactoring.  Currently it does not include EpochX's Context-free-grammar (CFG) packages, though these can be integrated later.  GP evolution configuration has been modified to use the an Objenome dependency-injection container internally.  Javassist dynamic bytecode generation can replace unimplemented abstract methods of arbitrary Java classes with procedures and expressions evolved to maximize a provided fitness function.
