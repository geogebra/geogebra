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
 * Thrown when a visitor encounters an error while processing a matrix entry.
 * @version $Revision: 729673 $ $Date: 2008-12-27 15:55:12 -0500 (Sat, 27 Dec 2008) $
 */
public class MatrixVisitorException extends MathRuntimeException {

    /** Serializable version identifier */
    private static final long serialVersionUID = 3814333035048617048L;

    /**
     * Constructs a new instance with specified formatted detail message.
     * @param pattern format specifier
     * @param arguments format arguments
     */
    public MatrixVisitorException(final String pattern, final Object[] arguments) {
      super(pattern, arguments);
    }

}
