Evolutionary Dependency Injection
------------------------------

A [dependency injection](https://en.wikipedia.org/wiki/Dependency_injection) container that automatically assembles software from combinations of components.  Ambiguities in the choice of particular dependencies form a set of combinatorial parameters that can be optimized to maximize a goal function.  In other words, the container can potentially grow itself.  It can also be used in a deterministic mode as a minimal dependency injection framework.


![base](https://raw.githubusercontent.com/automenta/objenome/master/objenome.jpg)

Hyperparameter Optimization
---------------------------
This allows [hyperparameter optimization](https://en.wikipedia.org/wiki/Hyperparameter_optimization) of arbitrary APIs by automatic application of evolutionary, and other kinds of search, directly to any given set of software components (ex: java classes) through their API.  

The numeric and enumerated parameters (objenes) required by the container's components form a genotype (objenome) that can be used to produce an abstractly-specified set of target instances (the phenotype).

Powered By..
------------

At its core, Objenome contains a refactored and generalized version of [MentaContainer](http://mentacontainer.soliveirajr.com/mtw/Page/Intro/en/mentacontainer-overview), which provided a minimal and straightforward DI container.

[Apache Commons Math - Genetic](http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/genetics/package-summary.html) package.

An adapted version of the Genetic Programming library [EpochX 2.0](https://github.com/tc33/) is included in the 'evolve' packages.  There are significant architectural differences including the elimination of all static classes and refactoring.  Currently it does not include EpochX's Context-free-grammar (CFG) packages, though these can be integrated later.
