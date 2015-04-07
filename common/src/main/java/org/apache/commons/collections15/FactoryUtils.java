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

import org.apache.commons.collections15.functors.ConstantFactory;
import org.apache.commons.collections15.functors.ExceptionFactory;
import org.apache.commons.collections15.functors.InstantiateFactory;
import org.apache.commons.collections15.functors.PrototypeFactory;

/**
 * <code>FactoryUtils</code> provides reference implementations and utilities
 * for the Factory functor interface. The supplied factories are:
 * <ul>
 * <li>Prototype - clones a specified object
 * <li>Reflection - creates objects using reflection
 * <li>Constant - always returns the same object
 * <li>Null - always returns null
 * <li>Exception - always throws an exception
 * </ul>
 * All the supplied factories are Serializable.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 3.0
 */
public class FactoryUtils {

    /**
     * This class is not normally instantiated.
     */
    public FactoryUtils() {
        super();
    }

    /**
     * Gets a Factory that always throws an exception.
     * This could be useful during testing as a placeholder.
     *
     * @return the factory
     * @see org.apache.commons.collections15.functors.ExceptionFactory
     */
    public static Factory exceptionFactory() {
        return ExceptionFactory.INSTANCE;
    }

    /**
     * Gets a Factory that will return null each time the factory is used.
     * This could be useful during testing as a placeholder.
     *
     * @return the factory
     * @see org.apache.commons.collections15.functors.ConstantFactory
     */
    public static Factory nullFactory() {
        return ConstantFactory.NULL_INSTANCE;
    }

    /**
     * Creates a Factory that will return the same object each time the factory
     * is used. No check is made that the object is immutable. In general, only
     * immutable objects should use the constant factory. Mutable objects should
     * use the prototype factory.
     *
     * @param constantToReturn the constant object to return each time in the factory
     * @return the <code>constant</code> factory.
     * @see org.apache.commons.collections15.functors.ConstantFactory
     */
    public static <T> Factory<T> constantFactory(T constantToReturn) {
        return ConstantFactory.getInstance(constantToReturn);
    }

    /**
     * Creates a Factory that will return a clone of the same prototype object
     * each time the factory is used. The prototype will be cloned using one of these
     * techniques (in order):
     * <ul>
     * <li>public clone method
     * <li>public copy constructor
     * <li>serialization clone
     * <ul>
     *
     * @param prototype the object to clone each time in the factory
     * @return the <code>prototype</code> factory
     * @throws IllegalArgumentException if the prototype is null
     * @throws IllegalArgumentException if the prototype cannot be cloned
     * @see org.apache.commons.collections15.functors.PrototypeFactory
     */
    public static <T> Factory<T> prototypeFactory(T prototype) {
        return PrototypeFactory.getInstance(prototype);
    }

    /**
     * Creates a Factory that can create objects of a specific type using
     * a no-args constructor.
     *
     * @param classToInstantiate the Class to instantiate each time in the factory
     * @return the <code>reflection</code> factory
     * @throws IllegalArgumentException if the classToInstantiate is null
     * @see org.apache.commons.collections15.functors.InstantiateFactory
     */
    public static <T> Factory<T> instantiateFactory(Class<T> classToInstantiate) {
        return InstantiateFactory.getInstance(classToInstantiate, null, null);
    }

    /**
     * Creates a Factory that can create objects of a specific type using
     * the arguments specified to this method.
     *
     * @param classToInstantiate the Class to instantiate each time in the factory
     * @param paramTypes         parameter types for the constructor, can be null
     * @param args               the arguments to pass to the constructor, can be null
     * @return the <code>reflection</code> factory
     * @throws IllegalArgumentException if the classToInstantiate is null
     * @throws IllegalArgumentException if the paramTypes and args don't match
     * @throws IllegalArgumentException if the constructor doesn't exist
     * @see org.apache.commons.collections15.functors.InstantiateFactory
     */
    public static <T> Factory<T> instantiateFactory(Class<T> classToInstantiate, Class[] paramTypes, Object[] args) {
        return InstantiateFactory.getInstance(classToInstantiate, paramTypes, args);
    }

}
