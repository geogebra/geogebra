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
package org.apache.commons.math;

/**
 * Exception thrown when an error occurs evaluating a function.
 * <p>
 * Maintains an <code>argument</code> property holding the input value that
 * caused the function evaluation to fail.
 *
 * @version $Revision: 885278 $ $Date: 2009-11-29 16:47:51 -0500 (Sun, 29 Nov 2009) $
 */
public class FunctionEvaluationException extends MathException  {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -4305020489115478365L;

    /** Message for failed evaluation. */
    private static final String FAILED_EVALUATION_MESSAGE =
        "evaluation failed for argument = {0}";

    /** Argument causing function evaluation failure */
    private double[] argument;

    /**
     * Construct an exception indicating the argument value
     * that caused the function evaluation to fail.
     *
     * @param argument  the failing function argument
     */
    public FunctionEvaluationException(double argument) {
        super(FAILED_EVALUATION_MESSAGE, argument);
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
        super(FAILED_EVALUATION_MESSAGE, new Object());/*AGArrayRealVector(argument)*/;
        this.argument = argument;
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
        super(pattern, arguments);
        this.argument = argument;
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
        this.argument = argument;
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
        super(cause, pattern, arguments);
        this.argument = argument;
    }

    /**
     * Returns the function argument that caused this exception.
     *
     * @return  argument that caused function evaluation to fail
     */
    public double[] getArgument() {
        return argument;
    }

}
