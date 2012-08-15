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

import org.apache.commons.collections15.functors.AllPredicate;
import org.apache.commons.collections15.functors.AndPredicate;
import org.apache.commons.collections15.functors.AnyPredicate;
import org.apache.commons.collections15.functors.EqualPredicate;
import org.apache.commons.collections15.functors.ExceptionPredicate;
import org.apache.commons.collections15.functors.FalsePredicate;
import org.apache.commons.collections15.functors.IdentityPredicate;
import org.apache.commons.collections15.functors.InstanceofPredicate;
import org.apache.commons.collections15.functors.InvokerTransformer;
import org.apache.commons.collections15.functors.NonePredicate;
import org.apache.commons.collections15.functors.NotNullPredicate;
import org.apache.commons.collections15.functors.NotPredicate;
import org.apache.commons.collections15.functors.NullIsExceptionPredicate;
import org.apache.commons.collections15.functors.NullIsFalsePredicate;
import org.apache.commons.collections15.functors.NullIsTruePredicate;
import org.apache.commons.collections15.functors.NullPredicate;
import org.apache.commons.collections15.functors.OnePredicate;
import org.apache.commons.collections15.functors.OrPredicate;
import org.apache.commons.collections15.functors.TransformedPredicate;
import org.apache.commons.collections15.functors.TransformerPredicate;
import org.apache.commons.collections15.functors.TruePredicate;
import org.apache.commons.collections15.functors.UniquePredicate;

/**
 * <code>PredicateUtils</code> provides reference implementations and utilities
 * for the Predicate functor interface. The supplied predicates are:
 * <ul>
 * <li>Invoker - returns the result of a method call on the input object
 * <li>InstanceOf - true if the object is an instanceof a class
 * <li>Equal - true if the object equals() a specified object
 * <li>Identity - true if the object == a specified object
 * <li>Null - true if the object is null
 * <li>NotNull - true if the object is not null
 * <li>Unique - true if the object has not already been evaluated
 * <li>And/All - true if all of the predicates are true
 * <li>Or/Any - true if any of the predicates is true
 * <li>Either/One - true if only one of the predicate is true
 * <li>Neither/None - true if none of the predicates are true
 * <li>Not - true if the predicate is false, and vice versa
 * <li>Transformer - wraps a Transformer as a Predicate
 * <li>True - always return true
 * <li>False - always return false
 * <li>Exception - always throws an exception
 * <li>NullIsException/NullIsFalse/NullIsTrue - check for null input
 * <li>Transformed - transforms the input before calling the predicate
 * </ul>
 * All the supplied predicates are Serializable.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Ola Berg
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 3.0
 */
public class PredicateUtils {

    /**
     * This class is not normally instantiated.
     */
    public PredicateUtils() {
        super();
    }

    // Simple predicates
    //-----------------------------------------------------------------------------

    /**
     * Gets a Predicate that always throws an exception.
     * This could be useful during testing as a placeholder.
     *
     * @return the predicate
     * @see org.apache.commons.collections15.functors.ExceptionPredicate
     */
    public static Predicate exceptionPredicate() {
        return ExceptionPredicate.INSTANCE;
    }

    /**
     * Gets a Predicate that always returns true.
     *
     * @return the predicate
     * @see org.apache.commons.collections15.functors.TruePredicate
     */
    public static <T> Predicate<T> truePredicate() {
        return TruePredicate.getInstance();
    }

    /**
     * Gets a Predicate that always returns false.
     *
     * @return the predicate
     * @see org.apache.commons.collections15.functors.FalsePredicate
     */
    public static <T> Predicate<T> falsePredicate() {
        return FalsePredicate.getInstance();
    }

    /**
     * Gets a Predicate that checks if the input object passed in is null.
     *
     * @return the predicate
     * @see org.apache.commons.collections15.functors.NullPredicate
     */
    public static <T> Predicate<T> nullPredicate() {
        return NullPredicate.getInstance();
    }

    /**
     * Gets a Predicate that checks if the input object passed in is not null.
     *
     * @return the predicate
     * @see org.apache.commons.collections15.functors.NotNullPredicate
     */
    public static <T> Predicate<T> notNullPredicate() {
        return NotNullPredicate.getInstance();
    }

    /**
     * Creates a Predicate that checks if the input object is equal to the
     * specified object using equals().
     *
     * @param value the value to compare against
     * @return the predicate
     * @see org.apache.commons.collections15.functors.EqualPredicate
     */
    public static <T> Predicate<T> equalPredicate(T value) {
        return EqualPredicate.getInstance(value);
    }

    /**
     * Creates a Predicate that checks if the input object is equal to the
     * specified object by identity.
     *
     * @param value the value to compare against
     * @return the predicate
     * @see org.apache.commons.collections15.functors.IdentityPredicate
     */
    public static <T> Predicate<T> identityPredicate(T value) {
        return IdentityPredicate.getInstance(value);
    }

    /**
     * Creates a Predicate that checks if the object passed in is of
     * a particular type, using instanceof. A <code>null</code> input
     * object will return <code>false</code>.
     *
     * @param type the type to check for, may not be null
     * @return the predicate
     * @throws IllegalArgumentException if the class is null
     * @see org.apache.commons.collections15.functors.InstanceofPredicate
     */
    @SuppressWarnings("unchecked")
    public static Predicate instanceofPredicate(Class type) {
        return InstanceofPredicate.getInstance(type);
    }

    /**
     * Creates a Predicate that returns true the first time an object is
     * encountered, and false if the same object is received
     * again. The comparison is by equals(). A <code>null</code> input object
     * is accepted and will return true the first time, and false subsequently
     * as well.
     *
     * @return the predicate
     * @see org.apache.commons.collections15.functors.UniquePredicate
     */
    public static <T> Predicate<T> uniquePredicate() {
        // must return new instance each time
        return UniquePredicate.getInstance();
    }

    /**
     * Creates a Predicate that invokes a method on the input object.
     * The method must return either a boolean or a non-null Boolean,
     * and have no parameters. If the input object is null, a
     * PredicateException is thrown.
     * <p/>
     * For example, <code>PredicateUtils.invokerPredicate("isEmpty");</code>
     * will call the <code>isEmpty</code> method on the input object to
     * determine the predicate result.
     *
     * @param methodName the method name to call on the input object, may not be null
     * @return the predicate
     * @throws IllegalArgumentException if the methodName is null.
     * @see org.apache.commons.collections15.functors.InvokerTransformer
     * @see org.apache.commons.collections15.functors.TransformerPredicate
     */
    public static Predicate invokerPredicate(String methodName) {
        // reuse transformer as it has caching - this is lazy really, should have inner class here
        return asPredicate(InvokerTransformer.getInstance(methodName));
    }

    /**
     * Creates a Predicate that invokes a method on the input object.
     * The method must return either a boolean or a non-null Boolean,
     * and have no parameters. If the input object is null, a
     * PredicateException is thrown.
     * <p/>
     * For example, <code>PredicateUtils.invokerPredicate("isEmpty");</code>
     * will call the <code>isEmpty</code> method on the input object to
     * determine the predicate result.
     *
     * @param methodName the method name to call on the input object, may not be null
     * @param paramTypes the parameter types
     * @param args       the arguments
     * @return the predicate
     * @throws IllegalArgumentException if the method name is null
     * @throws IllegalArgumentException if the paramTypes and args don't match
     * @see org.apache.commons.collections15.functors.InvokerTransformer
     * @see org.apache.commons.collections15.functors.TransformerPredicate
     */
    public static Predicate invokerPredicate(String methodName, Class[] paramTypes, Object[] args) {
        // reuse transformer as it has caching - this is lazy really, should have inner class here
        return asPredicate(InvokerTransformer.getInstance(methodName, paramTypes, args));
    }

    // Boolean combinations
    //-----------------------------------------------------------------------------

    /**
     * Create a new Predicate that returns true only if both of the specified
     * predicates are true.
     *
     * @param predicate1 the first predicate, may not be null
     * @param predicate2 the second predicate, may not be null
     * @return the <code>and</code> predicate
     * @throws IllegalArgumentException if either predicate is null
     * @see org.apache.commons.collections15.functors.AndPredicate
     */
    public static <T> Predicate<T> andPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
        return AndPredicate.<T>getInstance(predicate1, predicate2);
    }

    /**
     * Create a new Predicate that returns true only if all of the specified
     * predicates are true.
     *
     * @param predicates an array of predicates to check, may not be null
     * @return the <code>all</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the array is null
     * @see org.apache.commons.collections15.functors.AllPredicate
     */
    public static <T> Predicate<T> allPredicate(Predicate<? super T> ... predicates) {
        return AllPredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true only if all of the specified
     * predicates are true. The predicates are checked in iterator order.
     *
     * @param predicates a collection of predicates to check, may not be null
     * @return the <code>all</code> predicate
     * @throws IllegalArgumentException if the predicates collection is null
     * @throws IllegalArgumentException if the predicates collection has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the collection is null
     * @see org.apache.commons.collections15.functors.AllPredicate
     */
    public static <T> Predicate<T> allPredicate(Collection<Predicate<? super T>> predicates) {
        return AllPredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true if either of the specified
     * predicates are true.
     *
     * @param predicate1 the first predicate, may not be null
     * @param predicate2 the second predicate, may not be null
     * @return the <code>or</code> predicate
     * @throws IllegalArgumentException if either predicate is null
     * @see org.apache.commons.collections15.functors.OrPredicate
     */
    public static <T> Predicate<T> orPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
        return OrPredicate.<T>getInstance(predicate1, predicate2);
    }

    /**
     * Create a new Predicate that returns true if any of the specified
     * predicates are true.
     *
     * @param predicates an array of predicates to check, may not be null
     * @return the <code>any</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the array is null
     * @see org.apache.commons.collections15.functors.AnyPredicate
     */
    public static <T> Predicate<T> anyPredicate(Predicate<? super T> ... predicates) {
        return AnyPredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true if any of the specified
     * predicates are true. The predicates are checked in iterator order.
     *
     * @param predicates a collection of predicates to check, may not be null
     * @return the <code>any</code> predicate
     * @throws IllegalArgumentException if the predicates collection is null
     * @throws IllegalArgumentException if the predicates collection has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the collection is null
     * @see org.apache.commons.collections15.functors.AnyPredicate
     */
    public static <T> Predicate<T> anyPredicate(Collection<Predicate<? super T>> predicates) {
        return AnyPredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true if one, but not both, of the
     * specified predicates are true.
     *
     * @param predicate1 the first predicate, may not be null
     * @param predicate2 the second predicate, may not be null
     * @return the <code>either</code> predicate
     * @throws IllegalArgumentException if either predicate is null
     * @see org.apache.commons.collections15.functors.OnePredicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> eitherPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
        return onePredicate(new Predicate[]{predicate1, predicate2});
    }

    /**
     * Create a new Predicate that returns true if only one of the specified
     * predicates are true.
     *
     * @param predicates an array of predicates to check, may not be null
     * @return the <code>one</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the array is null
     * @see org.apache.commons.collections15.functors.OnePredicate
     */
    public static <T> Predicate<T> onePredicate(Predicate<? super T> ... predicates) {
        return OnePredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true if only one of the specified
     * predicates are true. The predicates are checked in iterator order.
     *
     * @param predicates a collection of predicates to check, may not be null
     * @return the <code>one</code> predicate
     * @throws IllegalArgumentException if the predicates collection is null
     * @throws IllegalArgumentException if the predicates collection has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the collection is null
     * @see org.apache.commons.collections15.functors.OnePredicate
     */
    public static <T> Predicate<T> onePredicate(Collection<Predicate<? super T>> predicates) {
        return OnePredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true if neither of the specified
     * predicates are true.
     *
     * @param predicate1 the first predicate, may not be null
     * @param predicate2 the second predicate, may not be null
     * @return the <code>neither</code> predicate
     * @throws IllegalArgumentException if either predicate is null
     * @see org.apache.commons.collections15.functors.NonePredicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> neitherPredicate(Predicate<? super T> predicate1, Predicate<? super T> predicate2) {
        return nonePredicate(new Predicate[]{predicate1, predicate2});
    }

    /**
     * Create a new Predicate that returns true if none of the specified
     * predicates are true.
     *
     * @param predicates an array of predicates to check, may not be null
     * @return the <code>none</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the array is null
     * @see org.apache.commons.collections15.functors.NonePredicate
     */
    public static <T> Predicate<T> nonePredicate(Predicate<? super T> ... predicates) {
        return NonePredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true if none of the specified
     * predicates are true. The predicates are checked in iterator order.
     *
     * @param predicates a collection of predicates to check, may not be null
     * @return the <code>none</code> predicate
     * @throws IllegalArgumentException if the predicates collection is null
     * @throws IllegalArgumentException if the predicates collection has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the collection is null
     * @see org.apache.commons.collections15.functors.NonePredicate
     */
    public static <T> Predicate<T> nonePredicate(Collection<Predicate<? super T>> predicates) {
        return NonePredicate.getInstance(predicates);
    }

    /**
     * Create a new Predicate that returns true if the specified predicate
     * returns false and vice versa.
     *
     * @param predicate the predicate to not
     * @return the <code>not</code> predicate
     * @throws IllegalArgumentException if the predicate is null
     * @see org.apache.commons.collections15.functors.NotPredicate
     */
    public static <T> Predicate<T> notPredicate(Predicate<T> predicate) {
        return NotPredicate.getInstance(predicate);
    }

    // Adaptors
    //-----------------------------------------------------------------------------

    /**
     * Create a new Predicate that wraps a Transformer. The Transformer must
     * return either Boolean.TRUE or Boolean.FALSE otherwise a PredicateException
     * will be thrown.
     *
     * @param transformer the transformer to wrap, may not be null
     * @return the transformer wrapping predicate
     * @throws IllegalArgumentException if the transformer is null
     * @see org.apache.commons.collections15.functors.TransformerPredicate
     */
    public static <T> Predicate<T> asPredicate(Transformer<T, Boolean> transformer) {
        return TransformerPredicate.getInstance(transformer);
    }

    // Null handlers
    //-----------------------------------------------------------------------------

    /**
     * Gets a Predicate that throws an exception if the input object is null,
     * otherwise it calls the specified Predicate. This allows null handling
     * behaviour to be added to Predicates that don't support nulls.
     *
     * @param predicate the predicate to wrap, may not be null
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null.
     * @see org.apache.commons.collections15.functors.NullIsExceptionPredicate
     */
    public static <T> Predicate<T> nullIsExceptionPredicate(Predicate<T> predicate) {
        return NullIsExceptionPredicate.getInstance(predicate);
    }

    /**
     * Gets a Predicate that returns false if the input object is null, otherwise
     * it calls the specified Predicate. This allows null handling behaviour to
     * be added to Predicates that don't support nulls.
     *
     * @param predicate the predicate to wrap, may not be null
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null.
     * @see org.apache.commons.collections15.functors.NullIsFalsePredicate
     */
    public static <T> Predicate<T> nullIsFalsePredicate(Predicate<T> predicate) {
        return NullIsFalsePredicate.getInstance(predicate);
    }

    /**
     * Gets a Predicate that returns true if the input object is null, otherwise
     * it calls the specified Predicate. This allows null handling behaviour to
     * be added to Predicates that don't support nulls.
     *
     * @param predicate the predicate to wrap, may not be null
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null.
     * @see org.apache.commons.collections15.functors.NullIsTruePredicate
     */
    public static <T> Predicate<T> nullIsTruePredicate(Predicate<T> predicate) {
        return NullIsTruePredicate.getInstance(predicate);
    }

    // Transformed
    //-----------------------------------------------------------------------
    /**
     * Creates a predicate that transforms the input object before passing it
     * to the predicate.
     *
     * @param transformer the transformer to call first
     * @param predicate   the predicate to call with the result of the transform
     * @return the predicate
     * @throws IllegalArgumentException if the transformer or the predicate is null
     * @see org.apache.commons.collections15.functors.TransformedPredicate
     * @since Commons Collections 3.1
     */
    public static <I,O> Predicate<I> transformedPredicate(Transformer<I, ? extends O> transformer, Predicate<? super O> predicate) {
        return TransformedPredicate.getInstance(transformer, predicate);
    }

}
