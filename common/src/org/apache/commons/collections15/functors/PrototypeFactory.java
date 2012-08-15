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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.FunctorException;

/**
 * Factory implementation that creates a new instance each time based on a prototype.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class PrototypeFactory <T> {

    /**
     * Factory method that performs validation.
     * <p/>
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
     */
    public static <T> Factory<T> getInstance(T prototype) {
        if (prototype == null) {
            return ConstantFactory.NULL_INSTANCE;
        }
        try {
            Method method = prototype.getClass().getMethod("clone", null);
            return new PrototypeCloneFactory<T>(prototype, method);

        } catch (NoSuchMethodException ex) {
            try {
                prototype.getClass().getConstructor(new Class[]{prototype.getClass()});
                return new InstantiateFactory<T>((Class<T>) prototype.getClass(), new Class[]{prototype.getClass()}, new Object[]{prototype});

            } catch (NoSuchMethodException ex2) {
                if (prototype instanceof Serializable) {
                    return new PrototypeSerializationFactory((Serializable) prototype);
                }
            }
        }
        throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     */
    private PrototypeFactory() {
        super();
    }

    // PrototypeCloneFactory
    //-----------------------------------------------------------------------
    /**
     * PrototypeCloneFactory creates objects by copying a prototype using the clone method.
     */
    static class PrototypeCloneFactory <T> implements Factory<T>, Serializable {

        /**
         * The serial version
         */
        static final long serialVersionUID = 5604271422565175555L;

        /**
         * The object to clone each time
         */
        private final T iPrototype;
        /**
         * The method used to clone
         */
        private transient Method iCloneMethod;

        /**
         * Constructor to store prototype.
         */
        private PrototypeCloneFactory(T prototype, Method method) {
            super();
            iPrototype = prototype;
            iCloneMethod = method;
        }

        /**
         * Find the Clone method for the class specified.
         */
        private void findCloneMethod() {
            try {
                iCloneMethod = iPrototype.getClass().getMethod("clone", null);

            } catch (NoSuchMethodException ex) {
                throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
            }
        }

        /**
         * Creates an object by calling the clone method.
         *
         * @return the new object
         */
        public T create() {
            // needed for post-serialization
            if (iCloneMethod == null) {
                findCloneMethod();
            }

            try {
                return (T) iCloneMethod.invoke(iPrototype, null);

            } catch (IllegalAccessException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method must be public", ex);
            } catch (InvocationTargetException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", ex);
            }
        }
    }

    // PrototypeSerializationFactory
    //-----------------------------------------------------------------------
    /**
     * PrototypeSerializationFactory creates objects by cloning a prototype using serialization.
     */
    static class PrototypeSerializationFactory <T extends Serializable> implements Factory<T>, Serializable {

        /**
         * The serial version
         */
        static final long serialVersionUID = -8704966966139178833L;

        /**
         * The object to clone via serialization each time
         */
        private final Serializable iPrototype;

        /**
         * Constructor to store prototype
         */
        private PrototypeSerializationFactory(Serializable prototype) {
            super();
            iPrototype = prototype;
        }

        /**
         * Creates an object using serialization.
         *
         * @return the new object
         */
        public T create() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            ByteArrayInputStream bais = null;
            try {
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(iPrototype);

                bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream in = new ObjectInputStream(bais);
                return (T) in.readObject();

            } catch (ClassNotFoundException ex) {
                throw new FunctorException(ex);
            } catch (IOException ex) {
                throw new FunctorException(ex);
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                } catch (IOException ex) {
                    // ignore
                }
                try {
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

}
