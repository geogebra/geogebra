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
package org.apache.commons.math.exception.util;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Class for constructing localized messages.
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
public class MessageFactory {
    /**
     * Class contains only static methods.
     */
    private MessageFactory() {}

    /**
     * Builds a message string by from a pattern and its arguments.
     *
     * @param locale Locale in which the message should be translated.
     * @param pattern Format specifier.
     * @param arguments Format arguments.
     * @return a localized message string.
     */
    public static String buildMessage(Locale locale,
                                      Localizable pattern,
                                      Object ... arguments) {
        return buildMessage(locale, null, pattern, arguments);
    }

    /**
     * Builds a message string by from two patterns (specific and general) and
     * an argument list.
     *
     * @param locale Locale in which the message should be translated.
     * @param specific Format specifier (may be null).
     * @param general Format specifier (may be null).
     * @param arguments Format arguments. They will be substituted in
     * <em>both</em> the {@code general} and {@code specific} format specifiers.
     * @return a localized message string.
     */
    public static String buildMessage(Locale locale,
                                      Localizable specific,
                                      Localizable general,
                                      Object ... arguments) {
        final StringBuilder sb = new StringBuilder();
        if (general != null) {
            final MessageFormat fmt = new MessageFormat(general.getLocalizedString(locale), locale);
            sb.append(fmt.format(arguments));
        }
        if (specific != null) {
            if (general != null) {
                sb.append(": ");
            }
            final MessageFormat fmt = new MessageFormat(specific.getLocalizedString(locale), locale);
            sb.append(fmt.format(arguments));
        }

        return sb.toString();
    }
}
