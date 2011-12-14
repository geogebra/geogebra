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
package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Error thrown when a numerical computation can not be performed because the
 * numerical result failed to converge to a finite value.
 *
 * @since 2.2
 * @version $Revision: 1026666 $ $Date: 2010-10-23 21:30:48 +0200 (sam. 23 oct. 2010) $
 */
public class ConvergenceException extends MathIllegalStateException {
    /** Serializable version Id. */
    private static final long serialVersionUID = 4330003017885151975L;

    /**
     * Construct the exception.
     */
    public ConvergenceException() {
        this(null);
    }
    /**
     * Construct the exception with a specific context.
     *
     * @param specific Specific contexte pattern.
     */
    public ConvergenceException(Localizable specific) {
        this(specific,
             LocalizedFormats.CONVERGENCE_FAILED,
             null);
    }
    /**
     * Construct the exception with a specific context and arguments.
     *
     * @param specific Specific contexte pattern.
     * @param args Arguments.
     */
    public ConvergenceException(Localizable specific,
                                Object ... args) {
        super(specific,
              LocalizedFormats.CONVERGENCE_FAILED,
              args);
    }
}
