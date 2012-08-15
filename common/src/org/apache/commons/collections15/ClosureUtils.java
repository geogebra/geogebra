// GenericsNote: Converted.
/*
 *  Copyright 2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections15;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.functors.ChainedClosure;
import org.apache.commons.collections15.functors.EqualPredicate;
import org.apache.commons.collections15.functors.ExceptionClosure;
import org.apache.commons.collections15.functors.ForClosure;
import org.apache.commons.collections15.functors.IfClosure;
import org.apache.commons.collections15.functors.InvokerTransformer;
import org.apache.commons.collections15.functors.NOPClosure;
import org.apache.commons.collections15.functors.SwitchClosure;
import org.apache.commons.collections15.functors.TransformerClosure;
import org.apache.commons.collections15.functors.WhileClosure;

/**
 * <code>ClosureUtils</code> provides reference implementations and utilities
 * for the Closure functor interface. The supplied closures are:
 * <ul>
 * <li>Invoker - invokes a method on the input object
 * <li>For - repeatedly calls a closure for a fixed number of times
 * <li>While - repeatedly calls a closure while a predicate is true
 * <li>DoWhile - repeatedly calls a closure while a predicate is true
 * <li>Chained - chains two or more closures together
 * <li>Switch - calls one closure based on one or more predicates
 * <li>SwitchMap - calls one closure looked up from a Map
 * <li>Transformer - wraps a Transformer as a Closure
 * <li>NOP - does nothing
 * <li>Exception - always throws an exception
 * </ul>
 * All the supplied closures are Serializable.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 3.0
 */
public class ClosureUtils {

    /**
     * This class is not normally instantiated.
     */
    public ClosureUtils() {
        super();
    }

    /**
     * Gets a Closure that always throws an exception.
     * This could be useful during testing as a placeholder.
     *
     * @return the closure
     * @see org.apache.commons.collections15.functors.ExceptionClosure
     */
    public static Closure exceptionClosure() {
        return ExceptionClosure.INSTANCE;
    }

    /**
     * Gets a Closure that will do nothing.
     * This could be useful during testing as a placeholder.
     *
     * @return the closure
     * @see org.apache.commons.collections15.functors.NOPClosure
     */
    public static Closure nopClosure() {
        return NOPClosure.INSTANCE;
    }

    /**
     * Creates a Closure that calls a Transformer each time it is called.
     * The transformer will be called using the closure's input object.
     * The transformer's result will be ignored.
     *
     * @param transformer the transformer to run each time in the closure, null means nop
     * @return the closure
     * @see org.apache.commons.collections15.functors.TransformerClosure
     */
    public static <I,O> Closure<I> asClosure(Transformer<I, O> transformer) {
        return TransformerClosure.getInstance(transformer);
    }

    /**
     * Creates a Closure that will call the closure <code>count</code> times.
     * <p/>
     * A null closure or zero count returns the <code>NOPClosure</code>.
     *
     * @param count   the number of times to loop
     * @param closure the closure to call repeatedly
     * @return the <code>for</code> closure
     * @see org.apache.commons.collections15.functors.ForClosure
     */
    public static <T> Closure<T> forClosure(int count, Closure<T> closure) {
        return ForClosure.getInstance(count, closure);
    }

    /**
     * Creates a Closure that will call the closure repeatedly until the
     * predicate returns false.
     *
     * @param predicate the predicate to use as an end of loop test, not null
     * @param closure   the closure to call repeatedly, not null
     * @return the <code>while</code> closure
     * @throws IllegalArgumentException if either argument is null
     * @see org.apache.commons.collections15.functors.WhileClosure
     */
    public static <T> Closure<T> whileClosure(Predicate<? super T> predicate, Closure<? super T> closure) {
        return WhileClosure.<T>getInstance(predicate, closure, false);
    }

    /**
     * Creates a Closure that will call the closure once and then repeatedly
     * until the predicate returns false.
     *
     * @param closure   the closure to call repeatedly, not null
     * @param predicate the predicate to use as an end of loop test, not null
     * @return the <code>do-while</code> closure
     * @throws IllegalArgumentException if either argument is null
     * @see org.apache.commons.collections15.functors.WhileClosure
     */
    public static <T> Closure<T> doWhileClosure(Closure<? super T> closure, Predicate<? super T> predicate) {
        return WhileClosure.<T>getInstance(predicate, closure, true);
    }

    /**
     * Creates a Closure that will invoke a specific method on the closure's
     * input object by reflection.
     *
     * @param methodName the name of the method
     * @return the <code>invoker</code> closure
     * @throws IllegalArgumentException if the method name is null
     * @see org.apache.commons.collections15.functors.InvokerTransformer
     * @see org.apache.commons.collections15.functors.TransformerClosure
     */
    public static Closure invokerClosure(String methodName) {
        // reuse transformer as it has caching - this is lazy really, should have inner class here
        return asClosure(InvokerTransformer.getInstance(methodName));
    }

    /**
     * Creates a Closure that will invoke a specific method on the closure's
     * input object by reflection.
     *
     * @param methodName the name of the method
     * @param paramTypes the parameter types
     * @param args       the arguments
     * @return the <code>invoker</code> closure
     * @throws IllegalArgumentException if the method name is null
     * @throws IllegalArgumentException if the paramTypes and args don't match
     * @see org.apache.commons.collections15.functors.InvokerTransformer
     * @see org.apache.commons.collections15.functors.TransformerClosure
     */
    public static Closure invokerClosure(String methodName, Class[] paramTypes, Object[] args) {
        // reuse transformer as it has caching - this is lazy really, should have inner class here
        return asClosure(InvokerTransformer.getInstance(methodName, paramTypes, args));
    }

    /**
     * Create a new Closure that calls two Closures, passing the result of
     * the first into the second.
     *
     * @param closure1 the first closure
     * @param closure2 the second closure
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if either closure is null
     * @see org.apache.commons.collections15.functors.ChainedClosure
     */
    public static <T> Closure<T> chainedClosure(Closure<T> closure1, Closure<T> closure2) {
        return ChainedClosure.<T>getInstance(closure1, closure2);
    }

    /**
     * Create a new Closure that calls each closure in turn, passing the
     * result into the next closure.
     *
     * @param closures an array of closures to chain
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures array is null
     * @throws IllegalArgumentException if any closure in the array is null
     * @see org.apache.commons.collections15.functors.ChainedClosure
     */
    public static <T> Closure<T> chainedClosure(Closure<T>[] closures) {
        return ChainedClosure.getInstance(closures);
    }

    /**
     * Create a new Closure that calls each closure in turn, passing the
     * result into the next closure. The ordering is that of the iterator()
     * method on the collection.
     *
     * @param closures a collection of closures to chain
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures collection is null
     * @throws IllegalArgumentException if the closures collection is empty
     * @throws IllegalArgumentException if any closure in the collection is null
     * @see org.apache.commons.collections15.functors.ChainedClosure
     */
    public static <T> Closure<T> chainedClosure(Collection<T> closures) {
        return ChainedClosure.getInstance(closures);
    }

    /**
     * Create a new Closure that calls one of two closures depending
     * on the specified predicate.
     *
     * @param predicate    the predicate to switch on
     * @param trueClosure  the closure called if the predicate is true
     * @param falseClosure the closure called if the predicate is false
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the predicate is null
     * @throws IllegalArgumentException if either closure is null
     * @see org.apache.commons.collections15.functors.IfClosure
     */
    public static <T> Closure<T> ifClosure(Predicate<? super T> predicate, Closure<? super T> trueClosure, Closure<? super T> falseClosure) {
        return IfClosure.<T>getInstance(predicate, trueClosure, falseClosure);
    }

    /**
     * Create a new Closure that calls one of the closures depending
     * on the predicates.
     * <p/>
     * The closure at array location 0 is called if the predicate at array
     * location 0 returned true. Each predicate is evaluated
     * until one returns true.
     *
     * @param predicates an array of predicates to check, not null
     * @param closures   an array of closures to call, not null
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the either array is null
     * @throws IllegalArgumentException if any element in the arrays is null
     * @throws IllegalArgumentException if the arrays are different sizes
     * @see org.apache.commons.collections15.functors.SwitchClosure
     */
    public static <T> Closure<T> switchClosure(Predicate<? super T>[] predicates, Closure<? super T>[] closures) {
        return SwitchClosure.<T>getInstance(predicates, closures, null);
    }

    /**
     * Create a new Closure that calls one of the closures depending
     * on the predicates.
     * <p/>
     * The closure at array location 0 is called if the predicate at array
     * location 0 returned true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * closure is called.
     *
     * @param predicates     an array of predicates to check, not null
     * @param closures       an array of closures to call, not null
     * @param defaultClosure the default to call if no predicate matches
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the either array is null
     * @throws IllegalArgumentException if any element in the arrays is null
     * @throws IllegalArgumentException if the arrays are different sizes
     * @see org.apache.commons.collections15.functors.SwitchClosure
     */
    public static <T> Closure<T> switchClosure(Predicate<? super T>[] predicates, Closure<? super T>[] closures, Closure<? super T> defaultClosure) {
        return SwitchClosure.<T>getInstance(predicates, closures, defaultClosure);
    }

    /**
     * Create a new Closure that calls one of the closures depending
     * on the predicates.
     * <p/>
     * The Map consists of Predicate keys and Closure values. A closure
     * is called if its matching predicate returns true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * closure is called. The default closure is set in the map with a
     * null key. The ordering is that of the iterator() method on the entryset
     * collection of the map.
     *
     * @param predicatesAndClosures a map of predicates to closures
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if the map is empty
     * @throws IllegalArgumentException if any closure in the map is null
     * @throws ClassCastException       if the map elements are of the wrong type
     * @see org.apache.commons.collections15.functors.SwitchClosure
     */
    public static <T> Closure<T> switchClosure(Map<Predicate<? super T>, Closure<? super T>> predicatesAndClosures) {
        return SwitchClosure.<T>getInstance(predicatesAndClosures);
    }

    /**
     * Create a new Closure that uses the input object as a key to find the
     * closure to call.
     * <p/>
     * The Map consists of object keys and Closure values. A closure
     * is called if the input object equals the key. If there is no match, the
     * default closure is called. The default closure is set in the map
     * using a null key.
     *
     * @param objectsAndClosures a map of objects to closures
     * @return the closure
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if the map is empty
     * @throws IllegalArgumentException if any closure in the map is null
     * @see org.apache.commons.collections15.functors.SwitchClosure
     */
    public static <T> Closure<T> switchMapClosure(Map<T, Closure<T>> objectsAndClosures) {
        Closure[] trs = null;
        Predicate[] preds = null;
        if (objectsAndClosures == null) {
            throw new IllegalArgumentException("The object and closure map must not be null");
        }
        Closure def = (Closure) objectsAndClosures.remove(null);
        int size = objectsAndClosures.size();
        trs = new Closure[size];
        preds = new Predicate[size];
        int i = 0;
        for (Iterator it = objectsAndClosures.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            preds[i] = EqualPredicate.getInstance(entry.getKey());
            trs[i] = (Closure) entry.getValue();
            i++;
        }
        return switchClosure(preds, trs, def);
    }

}
