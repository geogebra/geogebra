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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.collections15.FunctorException;
import org.apache.commons.collections15.Transformer;

/**
 * Transformer implementation that creates a new object instance by reflection.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class InstantiateTransformer implements Transformer<Class, Object>, Serializable {

    /**
     * The serial version
     */
    static final long serialVersionUID = 3786388740793356347L;

    /**
     * Singleton instance that uses the no arg constructor
     */
    public static final InstantiateTransformer NO_ARG_INSTANCE = new InstantiateTransformer();

    /**
     * The constructor parameter types
     */
    private final Class[] iParamTypes;
    /**
     * The constructor arguments
     */
    private final Object[] iArgs;

    /**
     * Transformer method that performs validation.
     *
     * @param paramTypes the constructor parameter types
     * @param args       the constructor arguments
     * @return an instantiate transformer
     */
    public static InstantiateTransformer getInstance(Class[] paramTypes, Object[] args) {
        if (((paramTypes == null) && (args != null)) || ((paramTypes != null) && (args == null)) || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length))) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }

        if (paramTypes == null || paramTypes.length == 0) {
            return NO_ARG_INSTANCE;
        } else {
            paramTypes = (Class[]) paramTypes.clone();
            args = (Object[]) args.clone();
        }
        return new InstantiateTransformer(paramTypes, args);
    }

    /**
     * Constructor for no arg instance.
     */
    private InstantiateTransformer() {
        super();
        iParamTypes = null;
        iArgs = null;
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param paramTypes the constructor parameter types, not cloned
     * @param args       the constructor arguments, not cloned
     */
    public InstantiateTransformer(Class[] paramTypes, Object[] args) {
        super();
        iParamTypes = paramTypes;
        iArgs = args;
    }

    /**
     * Transforms the input Class object to a result by instantiation.
     *
     * @param input the input object to transform
     * @return the transformed result
     */
    public Object transform(Class input) {
        try {
            Constructor con = ((Class) input).getConstructor(iParamTypes);
            return con.newInstance(iArgs);

        } catch (NoSuchMethodException ex) {
            throw new FunctorException("InstantiateTransformer: The constructor must exist and be public ");
        } catch (InstantiationException ex) {
            throw new FunctorException("InstantiateTransformer: InstantiationException", ex);
        } catch (IllegalAccessException ex) {
            throw new FunctorException("InstantiateTransformer: Constructor must be public", ex);
        } catch (InvocationTargetException ex) {
            throw new FunctorException("InstantiateTransformer: Constructor threw an exception", ex);
        }
    }

}
