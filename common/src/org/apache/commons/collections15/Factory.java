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

/**
 * Defines a functor interface implemented by classes that create objects.
 * <p/>
 * A <code>Factory</code> creates an object without using an input parameter.
 * If an input parameter is required, then {@link Transformer} is more appropriate.
 * <p/>
 * Standard implementations of common factories are provided by
 * {@link FactoryUtils}. These include factories that return a constant,
 * a copy of a prototype or a new instance.
 *
 * @author Arron Bates
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 2.1
 */
public interface Factory <T> {

    /**
     * Create a new object.
     *
     * @return a new object
     * @throws FunctorException (runtime) if the factory cannot create an object
     */
    public T create();

}
