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

package org.apache.commons.math.geometry;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.CompositeFormat;

/**
 * Formats a 3D vector in components list format "{x; y; z}".
 * <p>The prefix and suffix "{" and "}" and the separator "; " can be replaced by
 * any user-defined strings. The number format for components can be configured.</p>
 * <p>White space is ignored at parse time, even if it is in the prefix, suffix
 * or separator specifications. So even if the default separator does include a space
 * character that is used at format time, both input string "{1;1;1}" and
 * " { 1 ; 1 ; 1 } " will be parsed without error and the same vector will be
 * returned. In the second case, however, the parse position after parsing will be
 * just after the closing curly brace, i.e. just before the trailing space.</p>
 *
 * @version $Revision: 1003886 $ $Date: 2010-10-02 23:04:44 +0200 (sam. 02 oct. 2010) $
 */
public class Vector3DFormat extends CompositeFormat {

    /** Serializable version identifier */
    private static final long serialVersionUID = -5447606608652576301L;

    /** The default prefix: "{". */
    private static final String DEFAULT_PREFIX = "{";

    /** The default suffix: "}". */
    private static final String DEFAULT_SUFFIX = "}";

    /** The default separator: ", ". */
    private static final String DEFAULT_SEPARATOR = "; ";

    /** Prefix. */
    private final String prefix;

    /** Suffix. */
    private final String suffix;

    /** Separator. */
    private final String separator;

    /** Trimmed prefix. */
    private final String trimmedPrefix;

    /** Trimmed suffix. */
    private final String trimmedSuffix;

    /** Trimmed separator. */
    private final String trimmedSeparator;

    /** The format used for components. */
    private final NumberFormat format;

    /**
     * Create an instance with default settings.
     * <p>The instance uses the default prefix, suffix and separator:
     * "{", "}", and "; " and the default number format for components.</p>
     */
    public Vector3DFormat() {
        this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_SEPARATOR, getDefaultNumberFormat());
    }

    /**
     * Create an instance with a custom number format for components.
     * @param format the custom format for components.
     */
    public Vector3DFormat(final NumberFormat format) {
        this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_SEPARATOR, format);
    }

    /**
     * Create an instance with custom prefix, suffix and separator.
     * @param prefix prefix to use instead of the default "{"
     * @param suffix suffix to use instead of the default "}"
     * @param separator separator to use instead of the default "; "
     */
    public Vector3DFormat(final String prefix, final String suffix,
                          final String separator) {
        this(prefix, suffix, separator, getDefaultNumberFormat());
    }

    /**
     * Create an instance with custom prefix, suffix, separator and format
     * for components.
     * @param prefix prefix to use instead of the default "{"
     * @param suffix suffix to use instead of the default "}"
     * @param separator separator to use instead of the default "; "
     * @param format the custom format for components.
     */
    public Vector3DFormat(final String prefix, final String suffix,
                          final String separator, final NumberFormat format) {
        this.prefix      = prefix;
        this.suffix      = suffix;
        this.separator   = separator;
        trimmedPrefix    = prefix.trim();
        trimmedSuffix    = suffix.trim();
        trimmedSeparator = separator.trim();
        this.format      = format;
    }

    /**
     * Get the set of locales for which 3D vectors formats are available.
     * <p>This is the same set as the {@link NumberFormat} set.</p>
     * @return available 3D vector format locales.
     */
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }

    /**
     * Get the format prefix.
     * @return format prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the format suffix.
     * @return format suffix.
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Get the format separator between components.
     * @return format separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Get the components format.
     * @return components format.
     */
    public NumberFormat getFormat() {
        return format;
    }

    /**
     * Returns the default 3D vector format for the current locale.
     * @return the default 3D vector format.
     */
    public static Vector3DFormat getInstance() {
        return getInstance(Locale.getDefault());
    }

    /**
     * Returns the default 3D vector format for the given locale.
     * @param locale the specific locale used by the format.
     * @return the 3D vector format specific to the given locale.
     */
    public static Vector3DFormat getInstance(final Locale locale) {
        return new Vector3DFormat(getDefaultNumberFormat(locale));
    }

    /**
     * This static method calls {@link #format(Object)} on a default instance of
     * Vector3DFormat.
     *
     * @param v Vector3D object to format
     * @return A formatted vector
     */
    public static String formatVector3D(Vector3D v) {
        return getInstance().format(v);
    }

    /**
     * Formats a {@link Vector3D} object to produce a string.
     * @param vector the object to format.
     * @param toAppendTo where the text is to be appended
     * @param pos On input: an alignment field, if desired. On output: the
     *            offsets of the alignment field
     * @return the value passed in as toAppendTo.
     */
    public StringBuffer format(Vector3D vector, StringBuffer toAppendTo,
                               FieldPosition pos) {

        pos.setBeginIndex(0);
        pos.setEndIndex(0);

        // format prefix
        toAppendTo.append(prefix);

        // format components
        formatDouble(vector.getX(), format, toAppendTo, pos);
        toAppendTo.append(separator);
        formatDouble(vector.getY(), format, toAppendTo, pos);
        toAppendTo.append(separator);
        formatDouble(vector.getZ(), format, toAppendTo, pos);

        // format suffix
        toAppendTo.append(suffix);

        return toAppendTo;

    }

    /**
     * Formats a object to produce a string.
     * <p><code>obj</code> must be a  {@link Vector3D} object. Any other type of
     * object will result in an {@link IllegalArgumentException} being thrown.</p>
     * @param obj the object to format.
     * @param toAppendTo where the text is to be appended
     * @param pos On input: an alignment field, if desired. On output: the
     *            offsets of the alignment field
     * @return the value passed in as toAppendTo.
     * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
     * @throws IllegalArgumentException is <code>obj</code> is not a valid type.
     */
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
                               FieldPosition pos) {

        if (obj instanceof Vector3D) {
            return format( (Vector3D)obj, toAppendTo, pos);
        }

        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.CANNOT_FORMAT_INSTANCE_AS_3D_VECTOR,
                                                                  obj.getClass().getName());

    }

    /**
     * Parses a string to produce a {@link Vector3D} object.
     * @param source the string to parse
     * @return the parsed {@link Vector3D} object.
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    public Vector3D parse(String source) throws ParseException {
        ParsePosition parsePosition = new ParsePosition(0);
        Vector3D result = parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw MathRuntimeException.createParseException(
                    parsePosition.getErrorIndex(),
                    LocalizedFormats.UNPARSEABLE_3D_VECTOR, source);
        }
        return result;
    }

    /**
     * Parses a string to produce a {@link Vector3D} object.
     * @param source the string to parse
     * @param pos input/ouput parsing parameter.
     * @return the parsed {@link Vector3D} object.
     */
    public Vector3D parse(String source, ParsePosition pos) {
        int initialIndex = pos.getIndex();

        // parse prefix
        parseAndIgnoreWhitespace(source, pos);
        if (!parseFixedstring(source, trimmedPrefix, pos)) {
            return null;
        }

        // parse X component
        parseAndIgnoreWhitespace(source, pos);
        Number x = parseNumber(source, format, pos);
        if (x == null) {
            // invalid abscissa
            // set index back to initial, error index should already be set
            pos.setIndex(initialIndex);
            return null;
        }

        // parse Y component
        parseAndIgnoreWhitespace(source, pos);
        if (!parseFixedstring(source, trimmedSeparator, pos)) {
            return null;
        }
        parseAndIgnoreWhitespace(source, pos);
        Number y = parseNumber(source, format, pos);
        if (y == null) {
            // invalid ordinate
            // set index back to initial, error index should already be set
            pos.setIndex(initialIndex);
            return null;
        }

        // parse Z component
        parseAndIgnoreWhitespace(source, pos);
        if (!parseFixedstring(source, trimmedSeparator, pos)) {
            return null;
        }
        parseAndIgnoreWhitespace(source, pos);
        Number z = parseNumber(source, format, pos);
        if (z == null) {
            // invalid height
            // set index back to initial, error index should already be set
            pos.setIndex(initialIndex);
            return null;
        }

        // parse suffix
        parseAndIgnoreWhitespace(source, pos);
        if (!parseFixedstring(source, trimmedSuffix, pos)) {
            return null;
        }

        return new Vector3D(x.doubleValue(), y.doubleValue(), z.doubleValue());

    }

    /**
     * Parses a string to produce a object.
     * @param source the string to parse
     * @param pos input/ouput parsing parameter.
     * @return the parsed object.
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }

}
