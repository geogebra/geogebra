// GenericsNote: Converted.
/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.apache.commons.collections15.functors;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Predicate;

/**
 * Closure implementation calls the closure whose predicate returns true,
 * like a switch statement.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class SwitchClosure <T> implements Closure<T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 3518477308466486130L;

    /**
     * The tests to consider
     */
    private final Predicate<? super T>[] iPredicates;
    /**
     * The matching closures to call
     */
    private final Closure<? super T>[] iClosures;
    /**
     * The default closure to call if no tests match
     */
    private final Closure<? super T> iDefault;

    /**
     * Factory method that performs validation and copies the parameter arrays.
     *
     * @param predicates     array of predicates, cloned, no nulls
     * @param closures       matching array of closures, cloned, no nulls
     * @param defaultClosure the closure to use if no match, null means nop
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if array is null
     * @throws IllegalArgumentException if any element in the array is null
     */
    public static <T> Closure<T> getInstance(Predicate<? super T>[] predicates, Closure<? super T>[] closures, Closure<? super T> defaultClosure) {
        FunctorUtils.validate(predicates);
        FunctorUtils.validate(closures);
        if (predicates.length != closures.length) {
            throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
        }
        if (predicates.length == 0) {
            return (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
        }
        predicates = FunctorUtils.copy(predicates);
        closures = FunctorUtils.copy(closures);
        return new SwitchClosure<T>(predicates, closures, defaultClosure);
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
     * @throws IllegalArgumentException if any closure in the map is null
     * @throws ClassCastException       if the map elements are of the wrong type
     */
    public static <T> Closure<T> getInstance(Map<Predicate<? super T>, Closure<? super T>> predicatesAndClosures) {
        Closure[] closures = null;
        Predicate[] preds = null;
        if (predicatesAndClosures == null) {
            throw new IllegalArgumentException("The predicate and closure map must not be null");
        }
        if (predicatesAndClosures.size() == 0) {
            return NOPClosure.INSTANCE;
        }
        // convert to array like this to guarantee iterator() ordering
        Closure defaultClosure = (Closure) predicatesAndClosures.remove(null);
        int size = predicatesAndClosures.size();
        if (size == 0) {
            return (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
        }
        closures = new Closure[size];
        preds = new Predicate[size];
        int i = 0;
        for (Iterator it = predicatesAndClosures.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            preds[i] = (Predicate<? super T>) entry.getKey();
            closures[i] = (Closure<? super T>) entry.getValue();
            i++;
        }
        return new SwitchClosure<T>(preds, closures, defaultClosure);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicates     array of predicates, not cloned, no nulls
     * @param closures       matching array of closures, not cloned, no nulls
     * @param defaultClosure the closure to use if no match, null means nop
     */
    public SwitchClosure(Predicate<? super T>[] predicates, Closure<? super T>[] closures, Closure<? super T> defaultClosure) {
        super();
        iPredicates = predicates;
        iClosures = closures;
        iDefault = (defaultClosure == null ? NOPClosure.INSTANCE : defaultClosure);
    }

    /**
     * Executes the closure whose matching predicate returns true
     *
     * @param input the input object
     */
    public void execute(T input) {
        for (int i = 0; i < iPredicates.length; i++) {
            if (iPredicates[i].evaluate(input) == true) {
                iClosures[i].execute(input);
                return;
            }
        }
        iDefault.execute(input);
    }

    /**
     * Gets the predicates, do not modify the array.
     *
     * @return the predicates
     * @since Commons Collections 3.1
     */
    public Predicate<? super T>[] getPredicates() {
        return iPredicates;
    }

    /**
     * Gets the closures, do not modify the array.
     *
     * @return the closures
     * @since Commons Collections 3.1
     */
    public Closure<? super T>[] getClosures() {
        return iClosures;
    }

    /**
     * Gets the default closure.
     *
     * @return the default closure
     * @since Commons Collections 3.1
     */
    public Closure<? super T> getDefaultClosure() {
        return iDefault;
    }

}
