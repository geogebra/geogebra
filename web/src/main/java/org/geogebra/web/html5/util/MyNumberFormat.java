/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.geogebra.web.html5.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import elemental2.core.JsNumber;
import jsinterop.base.Js;

/**
 * 
 * GWT NumberFormat class with Currency Code removed
 * 
 * 
 * Formats and parses numbers using locale-sensitive patterns.
 *
 * This class provides comprehensive and flexible support for a wide variety of
 * localized formats, including
 * <ul>
 * <li><b>Locale-specific symbols</b> such as decimal point, group separator,
 * digit representation, currency symbol, percent, and permill</li>
 * <li><b>Numeric variations</b> including integers ("123"), fixed-point numbers
 * ("123.4"), scientific notation ("1.23E4"), percentages ("12%"), and currency
 * amounts ("$123")</li>
 * <li><b>Predefined standard patterns</b> that can be used both for parsing and
 * formatting, including {@link #getDecimalFormat() decimal},
 * {@link #getPercentFormat() percentages}, and {@link #getScientificFormat()
 * scientific}</li>
 * <li><b>Custom patterns</b> and supporting features designed to make it
 * possible to parse and format numbers in any locale, including support for
 * Western, Arabic, and Indic digits</li>
 * </ul>
 *
 * <h3>Patterns</h3>
 * <p>
 * Formatting and parsing are based on customizable patterns that can include a
 * combination of literal characters and special characters that act as
 * placeholders and are replaced by their localized counterparts. Many
 * characters in a pattern are taken literally; they are matched during parsing
 * and output unchanged during formatting. Special characters, on the other
 * hand, stand for other characters, strings, or classes of characters. For
 * example, the '<code>#</code>' character is replaced by a localized digit.
 * </p>
 *
 * <p>
 * Often the replacement character is the same as the pattern character. In the
 * U.S. locale, for example, the '<code>,</code>' grouping character is replaced
 * by the same character '<code>,</code>'. However, the replacement is still
 * actually happening, and in a different locale, the grouping character may
 * change to a different character, such as '<code>.</code>'. Some special
 * characters affect the behavior of the formatter by their presence. For
 * example, if the percent character is seen, then the value is multiplied by
 * 100 before being displayed.
 * </p>
 *
 * <p>
 * The characters listed below are used in patterns. Localized symbols use the
 * corresponding characters taken from corresponding locale symbol collection,
 * which can be found in the properties files residing in the
 * <code><nobr>com.google.gwt.i18n.client.constants</nobr></code>. To insert a
 * special character in a pattern as a literal (that is, without any special
 * meaning) the character must be quoted. There are some exceptions to this
 * which are noted below.
 * </p>
 *
 * <table>
 * <tr>
 * <th>Symbol</th>
 * <th>Location</th>
 * <th>Localized?</th>
 * <th>Meaning</th>
 * </tr>
 *
 * <tr>
 * <td><code>0</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Digit</td>
 * </tr>
 *
 * <tr>
 * <td><code>#</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Digit, zero shows as absent</td>
 * </tr>
 *
 * <tr>
 * <td><code>.</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Decimal separator or monetary decimal separator</td>
 * </tr>
 *
 * <tr>
 * <td><code>-</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Minus sign</td>
 * </tr>
 *
 * <tr>
 * <td><code>,</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Grouping separator</td>
 * </tr>
 *
 * <tr>
 * <td><code>E</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Separates mantissa and exponent in scientific notation; need not be
 * quoted in prefix or suffix</td>
 * </tr>
 *
 * <tr>
 * <td><code>;</code></td>
 * <td>Subpattern boundary</td>
 * <td>Yes</td>
 * <td>Separates positive and negative subpatterns</td>
 * </tr>
 *
 * <tr>
 * <td><code>%</code></td>
 * <td>Prefix or suffix</td>
 * <td>Yes</td>
 * <td>Multiply by 100 and show as percentage</td>
 * </tr>
 *
 * <tr>
 * <td><nobr><code>\u2030</code> (\u005Cu2030)</nobr></td>
 * <td>Prefix or suffix</td>
 * <td>Yes</td>
 * <td>Multiply by 1000 and show as per mille</td>
 * </tr>
 *
 * <tr>
 * <td><nobr><code>\u00A4</code> (\u005Cu00A4)</nobr></td>
 * <td>Prefix or suffix</td>
 * <td>No</td>
 * <td>Currency sign, replaced by currency symbol; if doubled, replaced by
 * international currency symbol; if present in a pattern, the monetary decimal
 * separator is used instead of the decimal separator</td>
 * </tr>
 *
 * <tr>
 * <td><code>'</code></td>
 * <td>Prefix or suffix</td>
 * <td>No</td>
 * <td>Used to quote special characters in a prefix or suffix; for example,
 * <code>"'#'#"</code> formats <code>123</code> to <code>"#123"</code>; to
 * create a single quote itself, use two in succession, such as
 * <code>"# o''clock"</code></td>
 * </tr>
 *
 * </table>
 *
 * <p>
 * A <code>MyNumberFormat</code> pattern contains a postive and negative
 * subpattern separated by a semicolon, such as
 * <code>"#,##0.00;(#,##0.00)"</code>. Each subpattern has a prefix, a numeric
 * part, and a suffix. If there is no explicit negative subpattern, the negative
 * subpattern is the localized minus sign prefixed to the positive subpattern.
 * That is, <code>"0.00"</code> alone is equivalent to <code>"0.00;-0.00"</code>
 * . If there is an explicit negative subpattern, it serves only to specify the
 * negative prefix and suffix; the number of digits, minimal digits, and other
 * characteristics are ignored in the negative subpattern. That means that
 * <code>"#,##0.0#;(#)"</code> has precisely the same result as
 * <code>"#,##0.0#;(#,##0.0#)"</code>.
 * </p>
 *
 * <p>
 * The prefixes, suffixes, and various symbols used for infinity, digits,
 * thousands separators, decimal separators, etc. may be set to arbitrary
 * values, and they will appear properly during formatting. However, care must
 * be taken that the symbols and strings do not conflict, or parsing will be
 * unreliable. For example, the decimal separator and thousands separator should
 * be distinct characters, or parsing will be impossible.
 * </p>
 *
 * <p>
 * The grouping separator is a character that separates clusters of integer
 * digits to make large numbers more legible. It commonly used for thousands,
 * but in some locales it separates ten-thousands. The grouping size is the
 * number of digits between the grouping separators, such as 3 for "100,000,000"
 * or 4 for "1 0000 0000".
 * </p>
 *
 * <h3>Pattern Grammar (BNF)</h3>
 * <p>
 * The pattern itself uses the following grammar:
 * </p>
 *
 * <table>
 * <tr>
 * <td>pattern</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">subpattern ('<code>;</code>' subpattern)?
 * </td>
 * </tr>
 * <tr>
 * <td>subpattern</td>
 * <td>:=</td>
 * <td>prefix? number exponent? suffix?</td>
 * </tr>
 * <tr>
 * <td>number</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">(integer ('<code>.</code>' fraction)?) |
 * sigDigits</td>
 * </tr>
 * <tr>
 * <td>prefix</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>\u005Cu0000</code>'..'
 * <code>\u005CuFFFD</code>' - specialCharacters</td>
 * </tr>
 * <tr>
 * <td>suffix</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>\u005Cu0000</code>'..'
 * <code>\u005CuFFFD</code>' - specialCharacters</td>
 * </tr>
 * <tr>
 * <td>integer</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>#</code>'* '<code>0</code>'*'
 * <code>0</code>'</td>
 * </tr>
 * <tr>
 * <td>fraction</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>0</code>'* '<code>#</code>'*</td>
 * </tr>
 * <tr>
 * <td>sigDigits</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>#</code>'* '<code>@</code>''
 * <code>@</code>'* '<code>#</code>'*</td>
 * </tr>
 * <tr>
 * <td>exponent</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>E</code>' '<code>+</code>'? '
 * <code>0</code>'* '<code>0</code>'</td>
 * </tr>
 * <tr>
 * <td>padSpec</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>*</code>' padChar</td>
 * </tr>
 * <tr>
 * <td>padChar</td>
 * <td>:=</td>
 * <td>'<code>\u005Cu0000</code>'..'<code>\u005CuFFFD</code>' - quote</td>
 * </tr>
 * </table>
 *
 * <p>
 * Notation:
 * </p>
 *
 * <table>
 * <tr>
 * <td>X*</td>
 * <td style="white-space: nowrap">0 or more instances of X</td>
 * </tr>
 *
 * <tr>
 * <td>X?</td>
 * <td style="white-space: nowrap">0 or 1 instances of X</td>
 * </tr>
 *
 * <tr>
 * <td>X|Y</td>
 * <td style="white-space: nowrap">either X or Y</td>
 * </tr>
 *
 * <tr>
 * <td>C..D</td>
 * <td style="white-space: nowrap">any character from C up to D, inclusive</td>
 * </tr>
 *
 * <tr>
 * <td>S-T</td>
 * <td style="white-space: nowrap">characters in S, except those in T</td>
 * </tr>
 * </table>
 *
 * <p>
 * The first subpattern is for positive numbers. The second (optional)
 * subpattern is for negative numbers.
 * </p>
 *
 * <h3>Example</h3> {@example com.google.gwt.examples.NumberFormatExample}
 *
 *
 */
public class MyNumberFormat {

	/**
	 * Current NumberConstants interface to use, see
	 * {@link #setForcedLatinDigits(boolean)} for changing it.
	 */
	// Cached instances of standard formatters.
	private static MyNumberFormat cachedDecimalFormat;
	private static MyNumberFormat cachedPercentFormat;
	private static MyNumberFormat cachedScientificFormat;

	// Constants for characters used in programmatic (unlocalized) patterns.
	// private static final char CURRENCY_SIGN = '\u00A4';

	// Number constants mapped to use latin digits/separators.
	// private static NumberConstants latinNumberConstants = null;
	// Localized characters for dot and comma in number patterns, used to
	// produce
	// the latin mapping for arbitrary locales. Any separator not in either of
	// these strings will be mapped to non-breaking space (U+00A0).

	private static final char PATTERN_DECIMAL_SEPARATOR = '.';
	private static final char PATTERN_DIGIT = '#';
	private static final char PATTERN_EXPONENT = 'E';
	private static final char PATTERN_GROUPING_SEPARATOR = ',';
	private static final char PATTERN_MINUS = '-';
	private static final char PATTERN_PER_MILLE = '\u2030';
	private static final char PATTERN_PERCENT = '%';
	private static final char PATTERN_SEPARATOR = ';';
	private static final char PATTERN_ZERO_DIGIT = '0';

	private static final char QUOTE = '\'';
	/**
	 * Holds the current decimal position during one call to
	 * {@link #format(boolean, StringBuilder, int)}.
	 */
	private transient int decimalPosition;

	/**
	 * Forces the decimal separator to always appear in a formatted number.
	 */
	private boolean decimalSeparatorAlwaysShown = false;

	/**
	 * Holds the current digits length during one call to
	 * {@link #format(boolean, StringBuilder, int)}.
	 */
	private transient int digitsLength;

	/**
	 * Holds the current exponent during one call to
	 * {@link #format(boolean, StringBuilder, int)}.
	 */
	private transient int exponent;
	/**
	 * The number of digits between grouping separators in the integer portion
	 * of a number.
	 */
	private int groupingSize = 3;
	// private boolean isCurrencyFormat = false;
	private int maximumFractionDigits = 3; // invariant, >= minFractionDigits.

	private int maximumIntegerDigits = 40;

	private int minExponentDigits;

	private int minimumFractionDigits = 0;

	private int minimumIntegerDigits = 1;

	// The multiplier for use in percent, per mille, etc.
	private int multiplier = 1;

	private String negativePrefix = "-";

	private String negativeSuffix = "";

	// The pattern to use for formatting and parsing.
	private final String pattern;

	private String positivePrefix = "";

	private String positiveSuffix = "";

	// True to force the use of exponential (i.e. scientific) notation.
	private boolean useExponentialNotation = false;

	/**
	 * Returns true if all new MyNumberFormat instances will use latin digits
	 * and related characters rather than the localized ones.
	 * 
	 * @return true
	 */
	public static boolean forcedLatinDigits() {
		return true; // defaultNumberConstants != localizedNumberConstants;
	}

	/**
	 * Provides the standard decimal format for the default locale.
	 *
	 * @return a <code>MyNumberFormat</code> capable of producing and consuming
	 *         decimal format for the default locale
	 */
	public static MyNumberFormat getDecimalFormat() {
		if (cachedDecimalFormat == null) {
			cachedDecimalFormat = new MyNumberFormat(
					MyNumberConstants.decimalPattern);
		}
		return cachedDecimalFormat;
	}

	/**
	 * Gets a <code>MyNumberFormat</code> instance for the default locale using
	 * the specified pattern and the default currencyCode.
	 *
	 * @param pattern
	 *            pattern for this formatter
	 * @return a MyNumberFormat instance
	 * @throws IllegalArgumentException
	 *             if the specified pattern is invalid
	 */
	public static MyNumberFormat getFormat(String pattern) {
		return new MyNumberFormat(pattern);
	}

	/**
	 * Provides the standard percent format for the default locale.
	 *
	 * @return a <code>MyNumberFormat</code> capable of producing and consuming
	 *         percent format for the default locale
	 */
	public static MyNumberFormat getPercentFormat() {
		if (cachedPercentFormat == null) {
			cachedPercentFormat = new MyNumberFormat(
					MyNumberConstants.percentPattern);
		}
		return cachedPercentFormat;
	}

	/**
	 * Provides the standard scientific format for the default locale.
	 *
	 * @return a <code>MyNumberFormat</code> capable of producing and consuming
	 *         scientific format for the default locale
	 */
	public static MyNumberFormat getScientificFormat() {
		if (cachedScientificFormat == null) {
			cachedScientificFormat = new MyNumberFormat(
					MyNumberConstants.scientificPattern);
		}
		return cachedScientificFormat;
	}

	/**
	 * Appends a scaled string representation to a buffer, returning the scale
	 * (which is the number of places to the right of the end of the string the
	 * decimal point should be moved -- i.e., 3.5 would be added to the buffer
	 * as "35" and a returned scale of -1).
	 *
	 * @param buf
	 *            builder
	 * @param val
	 *            value
	 * @return scale to apply to the result
	 */
	// @VisibleForTesting
	int toScaledString(StringBuilder buf, double val) {
		int startLen = buf.length();

		String full = toPrecision(val, 20);

		buf.append(full);
		int scale = 0;

		// remove exponent if present, adjusting scale
		int expIdx = buf.indexOf("e", startLen);
		if (expIdx < 0) {
			expIdx = buf.indexOf("E", startLen);
		}
		if (expIdx >= 0) {
			int expDigits = expIdx + 1;
			if (expDigits < buf.length() && buf.charAt(expDigits) == '+') {
				++expDigits;
			}
			if (expDigits < buf.length()) {
				scale = Integer.parseInt(buf.substring(expDigits));
			}
			buf.delete(expIdx, buf.length());
		}

		// remove decimal point if present, adjusting scale
		int dot = buf.indexOf(".", startLen);
		if (dot >= 0) {
			buf.deleteCharAt(dot);
			scale -= buf.length() - dot;
		}
		return scale;
	}

	/**
	 * Convert a double to a string with {@code digits} precision. The resulting
	 * string may still be in exponential notation.
	 *
	 * @param d
	 *            double value
	 * @param digits
	 *            number of digits of precision to include
	 * @return non-localized string representation of {@code d}
	 */
	public static String toPrecision(double d, int digits) {
		JsNumber num = Js.uncheckedCast(d);
		return num.toPrecision(digits);
	}

	/**
	 * Constructs a format object based on the specified settings.
	 *
	 * @param pattern
	 *            pattern that specify how number should be formatted
	 */
	protected MyNumberFormat(String pattern) {
		this.pattern = pattern;

		parsePattern(this.pattern);
	}

	/**
	 * This method formats a double to produce a string.
	 *
	 * @param number0
	 *            The double to format
	 * @return the formatted number string
	 */
	public String format(double number0) {
		if (Double.isNaN(number0)) {
			return MyNumberConstants.notANumber;
		}
		boolean isNegative = ((number0 < 0.0)
				|| (number0 == 0.0 && 1 / number0 < 0.0));
		double number;
		if (isNegative) {
			number = -number0;
		} else {
			number = number0;
		}
		StringBuilder buf = new StringBuilder();
		if (Double.isInfinite(number)) {
			buf.append(isNegative ? negativePrefix : positivePrefix);
			buf.append(MyNumberConstants.infinity);
			buf.append(isNegative ? negativeSuffix : positiveSuffix);
			return buf.toString();
		}
		number *= multiplier;
		int scale = toScaledString(buf, number);

		// pre-round value to deal with .15 being represented as .149999... etc
		// check at 3 more digits than will be required in the output
		format(isNegative, buf, scale);
		return buf.toString();
	}

	/**
	 * This method formats a Number to produce a string.
	 * <p>
	 * Any {@link Number} which is not a {@link BigDecimal}, {@link BigInteger},
	 * or {@link Long} instance is formatted as a {@code double} value.
	 *
	 * @param number
	 *            The Number instance to format
	 * @return the formatted number string
	 */
	public String format(Number number) {
		if (number instanceof BigDecimal) {
			BigDecimal bigDec = (BigDecimal) number;
			boolean isNegative = bigDec.signum() < 0;
			if (isNegative) {
				bigDec = bigDec.negate();
			}
			bigDec = bigDec.multiply(BigDecimal.valueOf(multiplier));
			StringBuilder buf = new StringBuilder();
			buf.append(bigDec.unscaledValue().toString());
			format(isNegative, buf, -bigDec.scale());
			return buf.toString();
		} else if (number instanceof BigInteger) {
			BigInteger bigInt = (BigInteger) number;
			boolean isNegative = bigInt.signum() < 0;
			if (isNegative) {
				bigInt = bigInt.negate();
			}
			bigInt = bigInt.multiply(BigInteger.valueOf(multiplier));
			StringBuilder buf = new StringBuilder();
			buf.append(bigInt.toString());
			format(isNegative, buf, 0);
			return buf.toString();
		} else if (number instanceof Long) {
			return format(number.longValue(), 0);
		} else {
			return format(number.doubleValue());
		}
	}

	/**
	 * @return the pattern used by this number format.
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Change the number of fractional digits used for formatting with this
	 * instance.
	 * 
	 * @param digits
	 *            the exact number of fractional digits for formatted values;
	 *            must be >= 0
	 * @return {@code this}, for chaining purposes
	 */
	public MyNumberFormat overrideFractionDigits(int digits) {
		return overrideFractionDigits(digits, digits);
	}

	/**
	 * Change the number of fractional digits used for formatting with this
	 * instance. Digits after {@code minDigits} that are zero will be omitted
	 * from the formatted value.
	 * 
	 * @param minDigits
	 *            the minimum number of fractional digits for formatted values;
	 *            must be >= 0
	 * @param maxDigits
	 *            the maximum number of fractional digits for formatted values;
	 *            must be >= {@code minDigits}
	 * @return {@code this}, for chaining purposes
	 */
	public MyNumberFormat overrideFractionDigits(int minDigits, int maxDigits) {
		assert minDigits >= 0;
		assert maxDigits >= minDigits;
		minimumFractionDigits = minDigits;
		maximumFractionDigits = maxDigits;
		return this;
	}

	/**
	 * Format a number with its significant digits already represented in string
	 * form. This is done so both double and BigInteger/Decimal formatting can
	 * share code without requiring all users to pay the code size penalty for
	 * BigDecimal/etc.
	 * <p>
	 * Example values passed in:
	 * <ul>
	 * <li>-13e2 <br>
	 * {@code isNegative=true, digits="13", scale=2}
	 * <li>3.14158 <br>
	 * {@code isNegative=false, digits="314158", scale=-5}
	 * <li>.0001 <br>
	 * {@code isNegative=false, digits="1" ("0001" would be ok), scale=-4}
	 * </ul>
	 *
	 * @param isNegative
	 *            true if the value to be formatted is negative
	 * @param digits
	 *            a StringBuilder containing just the significant digits in the
	 *            value to be formatted, the formatted result will be left here
	 * @param scale
	 *            the number of places to the right the decimal point should be
	 *            moved in the digit string -- negative means the value contains
	 *            fractional digits
	 */
	protected void format(boolean isNegative, StringBuilder digits, int scale) {

		// Set these transient fields, which will be adjusted/used by the
		// routines
		// called in this method.
		exponent = 0;
		digitsLength = digits.length();
		decimalPosition = digitsLength + scale;

		boolean useExponent = this.useExponentialNotation;
		int currentGroupingSize = this.groupingSize;
		if (decimalPosition > 1024) {
			// force really large numbers to be in exponential form
			useExponent = true;
		}

		if (useExponent) {
			computeExponent(digits);
		}
		processLeadingZeros(digits);
		roundValue(digits);
		insertGroupingSeparators(digits, currentGroupingSize);
		adjustFractionDigits(digits);
		addZeroAndDecimal(digits);
		if (useExponent) {
			addExponent(digits);
			// the above call has invalidated digitsLength == digits.length()
		}

		// add prefix/suffix
		digits.insert(0, isNegative ? negativePrefix : positivePrefix);
		digits.append(isNegative ? negativeSuffix : positiveSuffix);
	}

	/**
	 * Parses text to produce a numeric value. A {@link NumberFormatException}
	 * is thrown if either the text is empty or if the parse does not consume
	 * all characters of the text.
	 *
	 * param text the string to be parsed return a parsed number value, which
	 * may be a Double, BigInteger, or BigDecimal throws NumberFormatException
	 * if the text segment could not be converted into a number
	 */
	// public Number parseBig(String text) throws NumberFormatException {
	// // TODO(jat): implement
	// return Double.valueOf(parse(text));
	// }

	/**
	 * Parses text to produce a numeric value.
	 *
	 * <p>
	 * The method attempts to parse text starting at the index given by pos. If
	 * parsing succeeds, then the index of <code>pos</code> is updated to the
	 * index after the last character used (parsing does not necessarily use all
	 * characters up to the end of the string), and the parsed number is
	 * returned. The updated <code>pos</code> can be used to indicate the
	 * starting point for the next call to this method. If an error occurs, then
	 * the index of <code>pos</code> is not changed.
	 * </p>
	 *
	 * param text the string to be parsed pparam inOutPos position to pass in
	 * and get back return a parsed number value, which may be a Double,
	 * BigInteger, or BigDecimal throws NumberFormatException if the text
	 * segment could not be converted into a number
	 */
	// public Number parseBig(String text, int[] inOutPos)
	// throws NumberFormatException {
	// // TODO(jat): implement
	// return Double.valueOf(parse(text, inOutPos));
	// }

	/**
	 * Format a possibly scaled long value.
	 *
	 * @param value0
	 *            value to format
	 * @param scale
	 *            the number of places to the right the decimal point should be
	 *            moved in the digit string -- negative means the value contains
	 *            fractional digits
	 * @return formatted value
	 */
	protected String format(long value0, int scale) {
		boolean isNegative = value0 < 0;
		double value;
		if (isNegative) {
			value = -value0;
		} else {
			value = value0;
		}
		value *= multiplier;
		StringBuilder buf = new StringBuilder();
		buf.append(String.valueOf(value));
		format(isNegative, buf, scale);
		return buf.toString();
	}

	/**
	 * Returns the number of digits between grouping separators in the integer
	 * portion of a number.
	 */
	protected int getGroupingSize() {
		return groupingSize;
	}

	/**
	 * Returns the prefix to use for negative values.
	 */
	protected String getNegativePrefix() {
		return negativePrefix;
	}

	/**
	 * Returns the suffix to use for negative values.
	 */
	protected String getNegativeSuffix() {
		return negativeSuffix;
	}

	/**
	 * Returns the prefix to use for positive values.
	 */
	protected String getPositivePrefix() {
		return positivePrefix;
	}

	/**
	 * Returns the suffix to use for positive values.
	 */
	protected String getPositiveSuffix() {
		return positiveSuffix;
	}

	/**
	 * Returns true if the decimal separator should always be shown.
	 */
	protected boolean isDecimalSeparatorAlwaysShown() {
		return decimalSeparatorAlwaysShown;
	}

	/**
	 * Add exponent suffix.
	 *
	 * @param digits
	 *            string builder
	 */
	private void addExponent(StringBuilder digits) {
		digits.append(MyNumberConstants.exponentialSymbol);
		if (exponent < 0) {
			exponent = -exponent;
			digits.append(MyNumberConstants.minusSign);
		}
		String exponentDigits = String.valueOf(exponent);
		for (int i = exponentDigits.length(); i < minExponentDigits; ++i) {
			digits.append('0');
		}
		digits.append(exponentDigits);
	}

	/**
	 * @param digits
	 *            string builder
	 */
	private void addZeroAndDecimal(StringBuilder digits) {
		// add zero and decimal point if required
		if (digitsLength == 0) {
			digits.insert(0, '0');
			++decimalPosition;
			++digitsLength;
		}
		if (decimalPosition < digitsLength || decimalSeparatorAlwaysShown) {
			digits.insert(decimalPosition, ".");
			++digitsLength;
		}
	}

	/**
	 * Adjust the fraction digits, adding trailing zeroes if necessary or
	 * removing excess trailing zeroes.
	 *
	 * @param digits
	 *            string builder
	 */
	private void adjustFractionDigits(StringBuilder digits) {
		// adjust fraction digits as required
		int requiredDigits = decimalPosition + minimumFractionDigits;
		if (digitsLength < requiredDigits) {
			// add trailing zeros
			while (digitsLength < requiredDigits) {
				digits.append('0');
				++digitsLength;
			}
		} else {
			// remove excess trailing zeros
			int toRemove = decimalPosition + maximumFractionDigits;
			if (toRemove > digitsLength) {
				toRemove = digitsLength;
			}
			while (toRemove > requiredDigits
					&& digits.charAt(toRemove - 1) == '0') {
				--toRemove;
			}
			if (toRemove < digitsLength) {
				digits.delete(toRemove, digitsLength);
				digitsLength = toRemove;
			}
		}
	}

	/**
	 * Compute the exponent to use and adjust decimal position if we are using
	 * exponential notation.
	 *
	 * @param digits
	 *            string builder
	 */
	private void computeExponent(StringBuilder digits) {
		// always trim leading zeros
		int strip = 0;
		while (strip < digitsLength - 1 && digits.charAt(strip) == '0') {
			++strip;
		}
		if (strip > 0) {
			digits.delete(0, strip);
			digitsLength -= strip;
			exponent -= strip;
		}

		// decimal should wind up between minimum & maximumIntegerDigits
		if (maximumIntegerDigits > minimumIntegerDigits
				&& maximumIntegerDigits > 0) {
			// in this case, the exponent should be a multiple of
			// maximumIntegerDigits and 1 <= decimal <= maximumIntegerDigits
			exponent += decimalPosition - 1;
			int remainder = exponent % maximumIntegerDigits;
			if (remainder < 0) {
				remainder += maximumIntegerDigits;
			}
			decimalPosition = remainder + 1;
			exponent -= remainder;
		} else {
			exponent += decimalPosition - minimumIntegerDigits;
			decimalPosition = minimumIntegerDigits;
		}

		// special-case 0 to have an exponent of 0
		if (digitsLength == 1 && digits.charAt(0) == '0') {
			exponent = 0;
			decimalPosition = minimumIntegerDigits;
		}
	}

	/**
	 * Insert grouping separators if needed.
	 *
	 * @param digits
	 *            string builder
	 * @param g
	 *            group size (or 0 to do nothing)
	 */
	private void insertGroupingSeparators(StringBuilder digits, int g) {
		if (g > 0) {
			for (int i = g; i < decimalPosition; i += g + 1) {
				digits.insert(decimalPosition - i, ",");
				++decimalPosition;
				++digitsLength;
			}
		}
	}

	/**
	 * This method parses affix part of pattern.
	 *
	 * @param pattern
	 *            pattern string that need to be parsed
	 * @param start
	 *            start position to parse
	 * @param affix
	 *            store the parsed result
	 * @param inNegativePattern
	 *            true if we are parsing the negative pattern and therefore only
	 *            care about the prefix and suffix
	 * @return how many characters parsed
	 */
	private int parseAffix(String pattern, int start, StringBuilder affix,
			boolean inNegativePattern) {
		affix.delete(0, affix.length());
		boolean inQuote = false;
		int len = pattern.length();

		for (int pos = start; pos < len; ++pos) {
			char ch = pattern.charAt(pos);
			if (ch == QUOTE) {
				if ((pos + 1) < len && pattern.charAt(pos + 1) == QUOTE) {
					++pos;
					affix.append("'"); // 'don''t'
				} else {
					inQuote = !inQuote;
				}
				continue;
			}

			if (inQuote) {
				affix.append(ch);
			} else {
				switch (ch) {
				case PATTERN_DIGIT:
				case PATTERN_ZERO_DIGIT:
				case PATTERN_GROUPING_SEPARATOR:
				case PATTERN_DECIMAL_SEPARATOR:
				case PATTERN_SEPARATOR:
					return pos - start;
				// case CURRENCY_SIGN:
				// isCurrencyFormat = true;
				// if ((pos + 1) < len
				// && pattern.charAt(pos + 1) == CURRENCY_SIGN) {
				// ++pos;
				// if (pos < len - 2
				// && pattern.charAt(pos + 1) == CURRENCY_SIGN
				// && pattern.charAt(pos + 2) == CURRENCY_SIGN) {
				// pos += 2;
				// affix.append(
				// currencyData.getSimpleCurrencySymbol());
				// } else {
				// affix.append(currencyData.getCurrencyCode());
				// }
				// } else {
				// affix.append(currencyData.getCurrencySymbol());
				// }
				// break;
				case PATTERN_PERCENT:
					if (!inNegativePattern) {
						if (multiplier != 1) {
							throw new IllegalArgumentException(
									"Too many percent/per mille characters in pattern \""
											+ pattern + '"');
						}
						multiplier = 100;
					}
					affix.append(MyNumberConstants.percent);
					break;
				case PATTERN_PER_MILLE:
					if (!inNegativePattern) {
						if (multiplier != 1) {
							throw new IllegalArgumentException(
									"Too many percent/per mille characters in pattern \""
											+ pattern + '"');
						}
						multiplier = 1000;
					}
					affix.append(MyNumberConstants.perMill);
					break;
				case PATTERN_MINUS:
					affix.append("-");
					break;
				default:
					affix.append(ch);
				}
			}
		}
		return len - start;
	}

	/**
	 * Method parses provided pattern, result is stored in member variables.
	 *
	 * @param pattern
	 *            pattern
	 */
	private void parsePattern(String pattern) {
		int pos = 0;
		StringBuilder affix = new StringBuilder();

		pos += parseAffix(pattern, pos, affix, false);
		positivePrefix = affix.toString();
		pos += parseTrunk(pattern, pos, false);
		pos += parseAffix(pattern, pos, affix, false);
		positiveSuffix = affix.toString();

		if (pos < pattern.length()
				&& pattern.charAt(pos) == PATTERN_SEPARATOR) {
			++pos;
			pos += parseAffix(pattern, pos, affix, true);
			negativePrefix = affix.toString();
			// the negative pattern is only used for prefix/suffix
			pos += parseTrunk(pattern, pos, true);
			pos += parseAffix(pattern, pos, affix, true);
			negativeSuffix = affix.toString();
		} else {
			negativePrefix = MyNumberConstants.minusSign + positivePrefix;
			negativeSuffix = positiveSuffix;
		}
	}

	/**
	 * This method parses the trunk part of a pattern.
	 *
	 * @param pattern
	 *            pattern string that need to be parsed
	 * @param start
	 *            where parse started
	 * @param ignorePattern
	 *            true if we are only parsing this for length and correctness,
	 *            such as in the negative portion of the pattern
	 * @return how many characters parsed
	 */
	private int parseTrunk(String pattern, int start, boolean ignorePattern) {
		int decimalPos = -1;
		int digitLeftCount = 0, zeroDigitCount = 0, digitRightCount = 0;
		byte groupingCount = -1;

		int len = pattern.length();
		int pos = start;
		boolean loop = true;
		for (; (pos < len) && loop; ++pos) {
			char ch = pattern.charAt(pos);
			switch (ch) {
			case PATTERN_DIGIT:
				if (zeroDigitCount > 0) {
					++digitRightCount;
				} else {
					++digitLeftCount;
				}
				if (groupingCount >= 0 && decimalPos < 0) {
					++groupingCount;
				}
				break;
			case PATTERN_ZERO_DIGIT:
				if (digitRightCount > 0) {
					throw new IllegalArgumentException(
							"Unexpected '0' in pattern \"" + pattern + '"');
				}
				++zeroDigitCount;
				if (groupingCount >= 0 && decimalPos < 0) {
					++groupingCount;
				}
				break;
			case PATTERN_GROUPING_SEPARATOR:
				groupingCount = 0;
				break;
			case PATTERN_DECIMAL_SEPARATOR:
				if (decimalPos >= 0) {
					throw new IllegalArgumentException(
							"Multiple decimal separators in pattern \""
									+ pattern + '"');
				}
				decimalPos = digitLeftCount + zeroDigitCount + digitRightCount;
				break;
			case PATTERN_EXPONENT:
				if (!ignorePattern) {
					if (useExponentialNotation) {
						throw new IllegalArgumentException(
								"Multiple exponential "
										+ "symbols in pattern \"" + pattern
										+ '"');
					}
					useExponentialNotation = true;
					minExponentDigits = 0;
				}

				// Use lookahead to parse out the exponential part
				// of the pattern, then jump into phase 2.
				while ((pos + 1) < len
						&& pattern.charAt(pos + 1) == PATTERN_ZERO_DIGIT) {
					++pos;
					if (!ignorePattern) {
						++minExponentDigits;
					}
				}

				if (!ignorePattern && (digitLeftCount + zeroDigitCount) < 1
						|| minExponentDigits < 1) {
					throw new IllegalArgumentException("Malformed exponential "
							+ "pattern \"" + pattern + '"');
				}
				loop = false;
				break;
			default:
				--pos;
				loop = false;
				break;
			}
		}

		if (zeroDigitCount == 0 && digitLeftCount > 0 && decimalPos >= 0) {
			// Handle "###.###" and "###." and ".###".
			int n = decimalPos;
			if (n == 0) { // Handle ".###"
				++n;
			}
			digitRightCount = digitLeftCount - n;
			digitLeftCount = n - 1;
			zeroDigitCount = 1;
		}

		// Do syntax checking on the digits.
		if ((decimalPos < 0 && digitRightCount > 0)
				|| (decimalPos >= 0 && (decimalPos < digitLeftCount
						|| decimalPos > (digitLeftCount + zeroDigitCount)))
				|| groupingCount == 0) {
			throw new IllegalArgumentException(
					"Malformed pattern \"" + pattern + '"');
		}

		if (ignorePattern) {
			return pos - start;
		}

		int totalDigits = digitLeftCount + zeroDigitCount + digitRightCount;

		maximumFractionDigits = (decimalPos >= 0 ? (totalDigits - decimalPos)
				: 0);
		if (decimalPos >= 0) {
			minimumFractionDigits = digitLeftCount + zeroDigitCount
					- decimalPos;
			if (minimumFractionDigits < 0) {
				minimumFractionDigits = 0;
			}
		}

		/*
		 * The effectiveDecimalPos is the position the decimal is at or would be
		 * at if there is no decimal. Note that if decimalPos<0, then
		 * digitTotalCount == digitLeftCount + zeroDigitCount.
		 */
		int effectiveDecimalPos = decimalPos >= 0 ? decimalPos : totalDigits;
		minimumIntegerDigits = effectiveDecimalPos - digitLeftCount;
		if (useExponentialNotation) {
			maximumIntegerDigits = digitLeftCount + minimumIntegerDigits;

			// In exponential display, integer part can't be empty.
			if (maximumFractionDigits == 0 && minimumIntegerDigits == 0) {
				minimumIntegerDigits = 1;
			}
		}

		this.groupingSize = (groupingCount > 0) ? groupingCount : 0;
		decimalSeparatorAlwaysShown = (decimalPos == 0
				|| decimalPos == totalDigits);

		return pos - start;
	}

	/**
	 * Remove excess leading zeros or add some if we don't have enough.
	 *
	 * @param digits
	 *            string builder
	 */
	private void processLeadingZeros(StringBuilder digits) {
		// make sure we have enough trailing zeros
		if (decimalPosition > digitsLength) {
			while (digitsLength < decimalPosition) {
				digits.append('0');
				++digitsLength;
			}
		}

		if (!useExponentialNotation) {
			// make sure we have the right number of leading zeros
			if (decimalPosition < minimumIntegerDigits) {
				// add leading zeros
				StringBuilder prefix = new StringBuilder();
				while (decimalPosition < minimumIntegerDigits) {
					prefix.append('0');
					++decimalPosition;
					++digitsLength;
				}
				digits.insert(0, prefix);
			} else if (decimalPosition > minimumIntegerDigits) {
				// trim excess leading zeros
				int strip = decimalPosition - minimumIntegerDigits;
				for (int i = 0; i < strip; ++i) {
					if (digits.charAt(i) != '0') {
						strip = i;
						break;
					}
				}
				if (strip > 0) {
					digits.delete(0, strip);
					digitsLength -= strip;
					decimalPosition -= strip;
				}
			}
		}
	}

	/**
	 * Propagate a carry from incrementing the {@code i+1}'th digit.
	 *
	 * @param digits
	 *            string builder
	 * @param k
	 *            digit to start incrementing
	 */
	private void propagateCarry(StringBuilder digits, final int k) {
		boolean carry = true;
		int i = k;
		while (carry && i >= 0) {
			char digit = digits.charAt(i);
			if (digit == '9') {
				// set this to zero and keep going
				digits.setCharAt(i--, '0');
			} else {
				digits.setCharAt(i, (char) (digit + 1));
				carry = false;
			}
		}
		if (carry) {
			// ran off the front, prepend a 1
			digits.insert(0, '1');
			++decimalPosition;
			++digitsLength;
		}
	}

	/**
	 * Round the value at the requested place, propagating any carry backward.
	 *
	 * @param digits
	 *            string bilder
	 */
	private void roundValue(StringBuilder digits) {
		// TODO(jat): other rounding modes?
		if (digitsLength > decimalPosition + maximumFractionDigits && digits
				.charAt(decimalPosition + maximumFractionDigits) >= '5') {
			int i = decimalPosition + maximumFractionDigits - 1;
			propagateCarry(digits, i);
		}
	}
}
