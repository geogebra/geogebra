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

import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Error thrown when a numerical computation can not be performed because the
 * numerical result failed to converge to a finite value.
 *
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 */
public class ConvergenceException extends MathException {

    /** Serializable version identifier */
    private static final long serialVersionUID = -1111352570797662604L;

    /**
     * Default constructor.
     */
    public ConvergenceException() {
        super(LocalizedFormats.CONVERGENCE_FAILED);
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     * @deprecated as of 2.2 replaced by {@link #ConvergenceException(Localizable, Object...)}
     */
    @Deprecated
    public ConvergenceException(String pattern, Object ... arguments) {
        this(new DummyLocalizable(pattern), arguments);
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public ConvergenceException(Localizable pattern, Object ... arguments) {
        super(pattern, arguments);
    }

    /**
     * Create an exception with a given root cause.
     * @param cause  the exception or error that caused this exception to be thrown
     */
    public ConvergenceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     * @deprecated as of 2.2 replaced by {@link #ConvergenceException(Throwable, Localizable, Object...)}
     */
    @Deprecated
    public ConvergenceException(Throwable cause, String pattern, Object ... arguments) {
        this(cause, new DummyLocalizable(pattern), arguments);
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public ConvergenceException(Throwable cause, Localizable pattern, Object ... arguments) {
        super(cause, pattern, arguments);
    }

}
