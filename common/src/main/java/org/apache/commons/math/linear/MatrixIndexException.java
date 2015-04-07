/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;

/**
 * Thrown when an operation addresses a matrix coordinate (row, col)
 * which is outside of the dimensions of a matrix.
 * @version $Revision: 1073255 $ $Date: 2011-02-22 09:42:06 +0100 (mar. 22 févr. 2011) $
 */
public class MatrixIndexException extends MathRuntimeException {

    /** Serializable version identifier */
    private static final long serialVersionUID = 8120540015829487660L;

    /**
     * Constructs a new instance with specified formatted detail message.
     * @param pattern format specifier
     * @param arguments format arguments
     * @deprecated as of 2.2 replaced by {@link #MatrixIndexException(Localizable, Object...)}
     */
    @Deprecated
    public MatrixIndexException(final String pattern, final Object ... arguments) {
      this(new DummyLocalizable(pattern), arguments);
    }

    /**
     * Constructs a new instance with specified formatted detail message.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public MatrixIndexException(final Localizable pattern, final Object ... arguments) {
      super(pattern, arguments);
    }

}
