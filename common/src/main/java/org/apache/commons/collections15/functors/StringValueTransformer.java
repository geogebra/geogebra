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

import org.apache.commons.collections15.Transformer;

/**
 * Transformer implementation that returns the <code>String.valueOf</code>.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class StringValueTransformer <T> implements Transformer<T, String>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 7511110693171758606L;

    /**
     * Factory returning the singleton instance.
     *
     * @return the singleton instance
     * @since Commons Collections 3.1
     */
    public static <T> Transformer<T, String> getInstance() {
        return new StringValueTransformer<T>();
    }

    /**
     * Restricted constructor.
     */
    private StringValueTransformer() {
        super();
    }

    /**
     * Transforms the input to result by calling <code>String.valueOf</code>.
     *
     * @param input the input object to transform
     * @return the transformed result
     */
    public String transform(T input) {
        return String.valueOf(input);
    }

}
