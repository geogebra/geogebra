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
/* This file was modified by GeoGebra Inc. */
package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;



/**
 * Thrown when a system attempts an operation on a matrix, and
 * that matrix does not satisfy the preconditions for the
 * aforementioned operation.
 * @version $Revision: 746578 $ $Date: 2009-02-21 15:01:14 -0500 (Sat, 21 Feb 2009) $
 */
public class InvalidMatrixException extends MathRuntimeException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1135533765052675495L;

    /**
     * Construct an exception with the given message.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.0
     */
    public InvalidMatrixException(final String pattern, final Object ... arguments) {
        super(pattern, arguments);
    }

    /**
     * Construct an exception with the given message.
     * @param cause the exception or error that caused this exception
     * to be thrown.
     * @since 2.0
     */
    public InvalidMatrixException(final Throwable cause) {
        super(cause);
    }

}
