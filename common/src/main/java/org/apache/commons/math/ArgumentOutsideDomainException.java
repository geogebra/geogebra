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

package org.apache.commons.math;

import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Error thrown when a method is called with an out of bounds argument.
 *
 * @since 1.2
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public class ArgumentOutsideDomainException extends FunctionEvaluationException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -4965972841162580234L;

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument
     * @param lower lower bound of the domain
     * @param upper upper bound of the domain
     */
    public ArgumentOutsideDomainException(double argument, double lower, double upper) {
        super(argument, LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN, argument, lower, upper);
    }

}
