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

package org.apache.commons.math.fraction;

import org.apache.commons.math.ConvergenceException;

/**
 * Error thrown when a double value cannot be converted to a fraction
 * in the allowed number of iterations.
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 13:36:48 -0400 (Sat, 05 Sep 2009) $
 * @since 1.2
 */
public class FractionConversionException extends ConvergenceException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -4661812640132576263L;

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param value double value to convert
     * @param maxIterations maximal number of iterations allowed
     */
    public FractionConversionException(double value, int maxIterations) {
        super("Unable to convert {0} to fraction after {1} iterations", value, maxIterations);
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param value double value to convert
     * @param p current numerator
     * @param q current denominator
     */
    public FractionConversionException(double value, long p, long q) {
        super("Overflow trying to convert {0} to fraction ({1}/{2})", value, p, q);
    }

}
