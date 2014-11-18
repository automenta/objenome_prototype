package objenome;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import objenome.impl.ClassBuilder;
import objenome.impl.SetterDependency;
import objenome.util.InjectionUtils;
import objenome.util.InjectionUtils.Provider;

/**
 * The implementation of the IoC container.
 *
 * @author sergio.oliveira.jr@gmail.com
 */
public class DefaultContext extends AbstractProtoContext implements Context {


    private Map<String, Object> singletonsCache = new HashMap<String, Object>();

    private Map<String, ThreadLocal<Object>> threadLocalsCache = new HashMap<String, ThreadLocal<Object>>();


    @Override
    public <T> T get(Object key) {

        String name = InjectionUtils.getKeyName(key);

        if (!builders.containsKey(name)) {
            return null;
        }

        Builder c = builders.get(name);

        Scope scope = scopes.get(name);

        Object target = null;

        try {

            if (scope == Scope.SINGLETON) {

                boolean needsToCreate = false;

                synchronized (this) {

                    if (singletonsCache.containsKey(name)) {

                        target = singletonsCache.get(name);

                        return (T) target; // no need to wire again...

                    } else {

                        needsToCreate = true;
                    }
                }

                if (needsToCreate) {

                    // instance needs to be in a non-synchronized block
                    target = c.instance(this);

                    checkInterceptable(c, target);

                    synchronized (this) {

                        singletonsCache.put(name, target);
                    }
                }

            } else if (scope == Scope.THREAD) {

                boolean needsToCreate = false;

                boolean needsToAddToCache = false;

                ThreadLocal<Object> t = null;

                synchronized (this) {

                    if (threadLocalsCache.containsKey(name)) {

                        t = threadLocalsCache.get(name);

                        target = t.get();

                        if (target == null) { // different thread...

                            needsToCreate = true;

                            // don't return... let it be wired...
                        } else {

                            return (T) target; // no need to wire again...

                        }

                    } else {

                        t = new ThreadLocal<Object>();

                        needsToCreate = true;

                        needsToAddToCache = true;

                        // let it be wired...
                    }
                }

                if (needsToCreate) {

                    // instance needs to be in a non-synchronized block
                    target = c.instance(this);

                    checkInterceptable(c, target);

                    t.set(target);
                }

                if (needsToAddToCache) {

                    synchronized (this) {

                        threadLocalsCache.put(name, t);
                    }
                }

            } else if (scope == Scope.NONE) {

                target = c.instance(this);

                checkInterceptable(c, target);

            } else {

                throw new UnsupportedOperationException("Don't know how to handle scope: " + scope);
            }

            if (target != null) {

                for (SetterDependency d : setterDependencies) {

                    // has dependency ?
                    Method m = d.check(target.getClass());

                    if (m != null) {

                        String sourceKey = d.getSource();

                        if (sourceKey.equals(name)) {

                            // cannot depend on itself... also avoid recursive StackOverflow...
                            continue;

                        }

                        Object source = get(sourceKey);

                        try {

                            // apply
                            m.invoke(target, source);

                        } catch (Exception e) {

                            throw new RuntimeException("Cannot inject dependency: method = " + (m != null ? m.getName() : "NULL") + " / source = "
                                    + (source != null ? source : "NULL") + " / target = " + target, e);

                        }
                    }
                }
            }

            return (T) target; // return target nicely with all the dependencies

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private final void checkInterceptable(Builder f, Object value) {

        if (f instanceof Interceptor) {

            Interceptor i = (Interceptor) f;

            ((Interceptor) f).onCreated(value);
        }
    }



    @Override
    public <T> T get(final Class<? extends T> c) {
        ClassBuilder f = getClassBuilder(c);
        return (T) f.instance(this);
    }

    @Override
    public <C> C apply(C instance) {

        Provider p = new Provider() {

            @Override
            public Object get(String key) {

                return DefaultContext.this.get(key);
            }

            @Override
            public boolean hasValue(String key) {

                return DefaultContext.this.contains(key);
            }

        };

        try {

            InjectionUtils.getObject(instance, p, false, null, true, false, true);

        } catch (Exception e) {

            throw new RuntimeException("Error populating bean: " + instance, e);
        }
        
        return instance;
    }

    @Override
    public Builder usable(Object key, Scope scope, Builder factory) {
        Builder b = super.usable(key, scope, factory);

        String keyString = InjectionUtils.getKeyName(key);
        singletonsCache.remove(keyString); // just in case we are overriding a previous singleton bean...
        ThreadLocal<Object> threadLocal = threadLocalsCache.remove(keyString); // just in case we are overriding a previous thread local...
        if (threadLocal != null) {
            threadLocal.remove();
        }
        return b;
    }

    
    @Override
    public void remove(Scope scope) {
        if (scope == Scope.SINGLETON) {
            List<ClearableHolder> listToClear = new LinkedList<ClearableHolder>();
            synchronized (this) {
                for (String key : singletonsCache.keySet()) {
                    Builder factory = builders.get(key);                    
                    if (factory instanceof Interceptor) {
                        Interceptor c = (Interceptor) factory;
                        Object value = singletonsCache.get(key);
                        listToClear.add(new ClearableHolder(c, value));
                    }                    
                }
                singletonsCache.clear();
            }
            // remove everything inside a non-synchronized block...
            for (ClearableHolder cp : listToClear) {
                cp.clear();
            }
        } else if (scope == Scope.THREAD) {
            List<ClearableHolder> listToClear = new LinkedList<ClearableHolder>();
            synchronized (this) {
                for (String key : threadLocalsCache.keySet()) {
                    Builder factory = builders.get(key);                    
                    if (factory instanceof Interceptor) {
                        Interceptor c = (Interceptor) factory;
                        ThreadLocal<Object> t = threadLocalsCache.get(key);
                        Object value = t.get();
                        // we are ONLY clearing if this thread has something in the threadlocal, in other words,
                        // if the thread has previously requested this key...
                        if (value != null) {
                            listToClear.add(new ClearableHolder(c, value));
                        }
                    }
                }
                // and now we remove all thread locals belonging to this thread...
                // this will only remove the instances related to this thread...
                for (ThreadLocal<Object> t : threadLocalsCache.values()) {
                    t.remove();
                }
            }
            // remove everything inside a non-synchronized block...
            for (ClearableHolder cp : listToClear) {
                cp.clear();
            }
        }
    }

    @Override
    public <T> T remove(Object k) {
        String key = InjectionUtils.getKeyName(k);
        if (!builders.containsKey(key)) {
            return null;
        }
        Scope scope = scopes.get(key);
        if (scope == Scope.SINGLETON) {
            ClearableHolder cp = null;
            Object value = null;
            synchronized (this) {
                value = singletonsCache.remove(key);
                if (value != null) {
                    Builder factory = builders.get(key);                    
                    if (factory instanceof Interceptor) {
                        Interceptor c = (Interceptor) factory;
                        cp = new ClearableHolder(c, value);
                    }
                }
            }
            if (cp != null) {
                cp.c.onRemoved(cp.value);
            }
            return (T) value;
        } else if (scope == Scope.THREAD) {
            ClearableHolder cp = null;
            Object retVal = null;
            synchronized (this) {
                ThreadLocal<Object> t = threadLocalsCache.get(key);
                if (t != null) {
                    Object o = t.get();
                    if (o != null) {
                        Builder factory = builders.get(key);                    
                        if (factory instanceof Interceptor) {
                            Interceptor c = (Interceptor) factory;
                            cp = new ClearableHolder(c, o);
                        }
                        t.remove();
                        retVal = o;
                    }
                }
            }
            if (cp != null) {
                cp.c.onRemoved(cp.value);
            }
            return (T) retVal;
        } else if (scope == Scope.NONE) {
            return null; // always...
        } else {
            throw new UnsupportedOperationException("Scope not supported: " + scope);
        }
    }


    @Override
    public synchronized boolean contains(Object obj) {
        String key = InjectionUtils.getKeyName(obj);
        if (!builders.containsKey(key)) {
            return false;
        }
        Scope scope = scopes.get(key);
        if (scope == Scope.NONE) {
            return false; // always...
        } else if (scope == Scope.SINGLETON) {
            return singletonsCache.containsKey(key);
        } else if (scope == Scope.THREAD) {
            ThreadLocal<Object> t = threadLocalsCache.get(key);
            if (t != null) {
                return t.get() != null;
            }
            return false;
        } else {
            throw new UnsupportedOperationException("This scope is not supported: " + scope);
        }
    }
        
}
