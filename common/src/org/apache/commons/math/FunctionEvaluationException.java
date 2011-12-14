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
import org.apache.commons.math.linear.ArrayRealVector;

/**
 * Exception thrown when an error occurs evaluating a function.
 * <p>
 * Maintains an <code>argument</code> property holding the input value that
 * caused the function evaluation to fail.
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public class FunctionEvaluationException extends MathException  {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1384427981840836868L;

    /** Argument causing function evaluation failure */
    private double[] argument;

    /**
     * Construct an exception indicating the argument value
     * that caused the function evaluation to fail.
     *
     * @param argument  the failing function argument
     */
    public FunctionEvaluationException(double argument) {
        super(LocalizedFormats.EVALUATION_FAILED, argument);
        this.argument = new double[] { argument };
    }

    /**
     * Construct an exception indicating the argument value
     * that caused the function evaluation to fail.
     *
     * @param argument  the failing function argument
     * @since 2.0
     */
    public FunctionEvaluationException(double[] argument) {
        super(LocalizedFormats.EVALUATION_FAILED, new ArrayRealVector(argument));
        this.argument = argument.clone();
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     */
    public FunctionEvaluationException(double argument,
                                       String pattern, Object ... arguments) {
        this(argument, new DummyLocalizable(pattern), arguments);
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public FunctionEvaluationException(double argument,
                                       Localizable pattern, Object ... arguments) {
        super(pattern, arguments);
        this.argument = new double[] { argument };
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.0
     */
    public FunctionEvaluationException(double[] argument,
                                       String pattern, Object ... arguments) {
        this(argument, new DummyLocalizable(pattern), arguments);
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public FunctionEvaluationException(double[] argument,
                                       Localizable pattern, Object ... arguments) {
        super(pattern, arguments);
        this.argument = argument.clone();
    }

    /**
     * Constructs an exception with specified root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param argument  the failing function argument
     * @since 1.2
     */
    public FunctionEvaluationException(Throwable cause, double argument) {
        super(cause);
        this.argument = new double[] { argument };
    }

    /**
     * Constructs an exception with specified root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param argument  the failing function argument
     * @since 2.0
     */
    public FunctionEvaluationException(Throwable cause, double[] argument) {
        super(cause);
        this.argument = argument.clone();
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     */
    public FunctionEvaluationException(Throwable cause,
                                       double argument, String pattern,
                                       Object ... arguments) {
        this(cause, argument, new DummyLocalizable(pattern), arguments);
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public FunctionEvaluationException(Throwable cause,
                                       double argument, Localizable pattern,
                                       Object ... arguments) {
        super(cause, pattern, arguments);
        this.argument = new double[] { argument };
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.0
     */
    public FunctionEvaluationException(Throwable cause,
                                       double[] argument, String pattern,
                                       Object ... arguments) {
        this(cause, argument, new DummyLocalizable(pattern), arguments);
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param cause  the exception or error that caused this exception to be thrown
     * @param argument  the failing function argument
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public FunctionEvaluationException(Throwable cause,
                                       double[] argument, Localizable pattern,
                                       Object ... arguments) {
        super(cause, pattern, arguments);
        this.argument = argument.clone();
    }

    /**
     * Returns the function argument that caused this exception.
     *
     * @return  argument that caused function evaluation to fail
     */
    public double[] getArgument() {
        return argument.clone();
    }
}
