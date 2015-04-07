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

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.FunctorException;

/**
 * Closure implementation that always throws an exception.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class ExceptionClosure <T> implements Closure<T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 7179106032121985545L;


    /**
     * Singleton predicate instance
     */
    public static final Closure INSTANCE = new ExceptionClosure();

    /**
     * Factory returning the singleton instance.
     *
     * @return the singleton instance
     * @since Commons Collections 3.1
     */
	public static <T> Closure<T> getInstance() {
        return INSTANCE;
    }

    /**
     * Restricted constructor.
     */
    private ExceptionClosure() {
        super();
    }

    /**
     * Always throw an exception.
     *
     * @param input the input object
     * @throws FunctorException always
     */
    public void execute(T input) {
        throw new FunctorException("ExceptionClosure invoked");
    }

}
