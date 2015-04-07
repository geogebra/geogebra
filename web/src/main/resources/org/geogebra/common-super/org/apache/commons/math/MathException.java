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

import org.geogebra.common.main.App;



/**
* Base class for commons-math checked exceptions.
* <p>
* Supports nesting, emulating JDK 1.4 behavior if necessary.</p>
* <p>
* Adapted from <a href="http://commons.apache.org/collections/api-release/org/apache/commons/collections/FunctorException.html"/>.</p>
*
* @version $Revision: 822850 $ $Date: 2009-10-07 14:56:42 -0400 (Wed, 07 Oct 2009) $
*/
public class MathException extends Exception {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -9004610152740737812L;

    /**
     * Pattern used to build the message.
     */
    private final String pattern;

    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * Constructs a new <code>MathException</code> with no
     * detail message.
     */
    public MathException() {
        this.pattern   = null;
        this.arguments = new Object[0];
    }

    /**
     * Constructs a new <code>MathException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     */
    public MathException(String pattern, Object ... arguments) {
      this.pattern   = pattern;
      this.arguments = (arguments == null) ? new Object[0] : arguments;
    }

    /**
     * Constructs a new <code>MathException</code> with specified
     * nested <code>Throwable</code> root cause.
     *
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public MathException(Throwable rootCause) {
        super(rootCause);
        this.pattern   = getMessage();
        this.arguments = new Object[0];
    }

    /**
     * Constructs a new <code>MathException</code> with specified
     * formatted detail message and nested <code>Throwable</code> root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     */
    public MathException(Throwable rootCause, String pattern, Object ... arguments) {
      super(rootCause);
      this.pattern   = pattern;
      this.arguments = (arguments == null) ? new Object[0] : arguments;
    }

    /**
     * Translate a string to a given locale.
     * @param s string to translate
     * @param locale locale into which to translate the string
     * @return translated string or original string
     * for unsupported locales or unknown strings
     */
    /*AGprivate static String translate(String s, Locale locale) {
        try {
            ResourceBundle bundle =
                    ResourceBundle.getBundle("org.apache.commons.math.MessagesResources", locale);
            if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                // the value of the resource is the translated string
                return bundle.getString(s);
            }

        } catch (MissingResourceException mre) {
            // do nothing here
        }

        // the locale is not supported or the resource is unknown
        // don't translate and fall back to using the string as is
        return s;

    }

    /** Gets the pattern used to build the message of this throwable.
     *
     * @return the pattern used to build the message of this throwable
     * @since 1.2
     */
    public String getPattern() {
        return pattern;
    }

    /** Gets the arguments used to build the message of this throwable.
     *
     * @return the arguments used to build the message of this throwable
     * @since 1.2
     */
    public Object[] getArguments() {
        return arguments;
    }

    /** Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated
     *
     * @return localized message
     * @since 1.2
     */
    /*AGpublic String getMessage(final Locale locale) {
        return (pattern == null) ? "" : new MessageFormat(translate(pattern, locale), locale).format(arguments);
    }

    /** {@inheritDoc} */
    /*AG@Override
    public String getMessage() {
        return getMessage(Locale.US);
    }

    /** {@inheritDoc} */
   /*AG @Override
    public String getLocalizedMessage() {
        return getMessage(Locale.getDefault());
    }

    /**
     * Prints the stack trace of this exception to the standard error stream.
     */
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace of this exception to the specified stream.
     *
     * @param out  the <code>PrintStream</code> to use for output
     */
   //AG @Override
    public void printStackTrace(/*AGPrintStream out*/String out) {
       /*AG synchronized (out) {
            PrintWriter pw = new PrintWriter(out, false);
            printStackTrace(pw);
            // Flush the PrintWriter before it's GC'ed.
            pw.flush();
        }*/
    	App.debug(out);
    }

	public String getSpecificPattern() {
		return pattern;
	}

	public Object getGeneralPattern() {
		return arguments;
	}

}
