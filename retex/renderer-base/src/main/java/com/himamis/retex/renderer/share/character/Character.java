/*
 * Copyright (c) 2002, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.himamis.retex.renderer.share.character;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

/**
 * The {@code Character} class wraps a value of the primitive type {@code char} in an object. An
 * object of type {@code Character} contains a single field whose type is {@code char}.
 * <p>
 * In addition, this class provides several methods for determining a character's category
 * (lowercase letter, digit, etc.) and for converting characters from uppercase to lowercase and
 * vice versa.
 * <p>
 * Character information is based on the Unicode Standard, version 6.0.0.
 * <p>
 * The methods and data of class {@code Character} are defined by the information in the
 * <i>UnicodeData</i> file that is part of the Unicode Character Database maintained by the Unicode
 * Consortium. This file specifies various properties including name and general category for every
 * defined Unicode code point or character range.
 * <p>
 * The file and its description are available from the Unicode Consortium at:
 * <ul>
 * <li><a href="http://www.unicode.org">http://www.unicode.org</a>
 * </ul>
 *
 * <h4><a name="unicode">Unicode Character Representations</a></h4>
 *
 * <p>
 * The {@code char} data type (and therefore the value that a {@code Character} object encapsulates)
 * are based on the original Unicode specification, which defined characters as fixed-width 16-bit
 * entities. The Unicode Standard has since been changed to allow for characters whose
 * representation requires more than 16 bits. The range of legal <em>code point</em>s is now U+0000
 * to U+10FFFF, known as <em>Unicode scalar value</em>. (Refer to the <a
 * href="http://www.unicode.org/reports/tr27/#notation"><i> definition</i></a> of the U+<i>n</i>
 * notation in the Unicode Standard.)
 *
 * <p>
 * <a name="BMP">The set of characters from U+0000 to U+FFFF is sometimes referred to as the
 * <em>Basic Multilingual Plane (BMP)</em>. <a name="supplementary">Characters</a> whose code points
 * are greater than U+FFFF are called <em>supplementary character</em>s. The Java platform uses the
 * UTF-16 representation in {@code char} arrays and in the {@code String} and {@code StringBuffer}
 * classes. In this representation, supplementary characters are represented as a pair of
 * {@code char} values, the first from the <em>high-surrogates</em> range, (&#92;uD800-&#92;uDBFF),
 * the second from the <em>low-surrogates</em> range (&#92;uDC00-&#92;uDFFF).
 *
 * <p>
 * A {@code char} value, therefore, represents Basic Multilingual Plane (BMP) code points, including
 * the surrogate code points, or code units of the UTF-16 encoding. An {@code int} value represents
 * all Unicode code points, including supplementary code points. The lower (least significant) 21
 * bits of {@code int} are used to represent Unicode code points and the upper (most significant) 11
 * bits must be zero. Unless otherwise specified, the behavior with respect to supplementary
 * characters and surrogate {@code char} values is as follows:
 *
 * <ul>
 * <li>The methods that only accept a {@code char} value cannot support supplementary characters.
 * They treat {@code char} values from the surrogate ranges as undefined characters. For example,
 * {@code Character.isLetter('\u005CuD840')} returns {@code false}, even though this specific value
 * if followed by any low-surrogate value in a string would represent a letter.
 *
 * <li>The methods that accept an {@code int} value support all Unicode characters, including
 * supplementary characters. For example, {@code Character.isLetter(0x2F81A)} returns {@code true}
 * because the code point value represents a letter (a CJK ideograph).
 * </ul>
 *
 * <p>
 * In the Java SE API documentation, <em>Unicode code point</em> is used for character values in the
 * range between U+0000 and U+10FFFF, and <em>Unicode code unit</em> is used for 16-bit {@code char}
 * values that are code units of the <em>UTF-16</em> encoding. For more information on Unicode
 * terminology, refer to the <a href="http://www.unicode.org/glossary/">Unicode Glossary</a>.
 *
 * @author Lee Boynton
 * @author Guy Steele
 * @author Akira Tanaka
 * @author Martin Buchholz
 * @author Ulf Zibis
 * @since 1.0
 */
public final class Character {
	/**
	 * The minimum radix available for conversion to and from strings. The constant value of this
	 * field is the smallest value permitted for the radix argument in radix-conversion methods such
	 * as the {@code digit} method, the {@code forDigit} method, and the {@code toString} method of
	 * class {@code Integer}.
	 *
	 * @see Character#digit(char, int)
	 * @see Character#forDigit(int, int)
	 * @see Integer#toString(int, int)
	 * @see Integer#valueOf(String)
	 */
	public static final int MIN_RADIX = 2;

	/**
	 * The maximum radix available for conversion to and from strings. The constant value of this
	 * field is the largest value permitted for the radix argument in radix-conversion methods such
	 * as the {@code digit} method, the {@code forDigit} method, and the {@code toString} method of
	 * class {@code Integer}.
	 *
	 * @see Character#digit(char, int)
	 * @see Character#forDigit(int, int)
	 * @see Integer#toString(int, int)
	 * @see Integer#valueOf(String)
	 */
	public static final int MAX_RADIX = 36;

	/**
	 * The constant value of this field is the smallest value of type {@code char},
	 * {@code '\u005Cu0000'}.
	 *
	 * @since 1.0.2
	 */
	public static final char MIN_VALUE = '\u0000';

	/**
	 * The constant value of this field is the largest value of type {@code char},
	 * {@code '\u005CuFFFF'}.
	 *
	 * @since 1.0.2
	 */
	public static final char MAX_VALUE = '\uFFFF';

	/*
	 * Normative general types
	 */

	/*
	 * General character types
	 */

	/**
	 * General category "Cn" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte UNASSIGNED = 0;

	/**
	 * General category "Lu" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte UPPERCASE_LETTER = 1;

	/**
	 * General category "Ll" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte LOWERCASE_LETTER = 2;

	/**
	 * General category "Lt" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte TITLECASE_LETTER = 3;

	/**
	 * General category "Lm" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte MODIFIER_LETTER = 4;

	/**
	 * General category "Lo" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_LETTER = 5;

	/**
	 * General category "Mn" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte NON_SPACING_MARK = 6;

	/**
	 * General category "Me" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte ENCLOSING_MARK = 7;

	/**
	 * General category "Mc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte COMBINING_SPACING_MARK = 8;

	/**
	 * General category "Nd" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte DECIMAL_DIGIT_NUMBER = 9;

	/**
	 * General category "Nl" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte LETTER_NUMBER = 10;

	/**
	 * General category "No" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_NUMBER = 11;

	/**
	 * General category "Zs" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte SPACE_SEPARATOR = 12;

	/**
	 * General category "Zl" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte LINE_SEPARATOR = 13;

	/**
	 * General category "Zp" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte PARAGRAPH_SEPARATOR = 14;

	/**
	 * General category "Cc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte CONTROL = 15;

	/**
	 * General category "Cf" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte FORMAT = 16;

	/**
	 * General category "Co" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte PRIVATE_USE = 18;

	/**
	 * General category "Cs" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte SURROGATE = 19;

	/**
	 * General category "Pd" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte DASH_PUNCTUATION = 20;

	/**
	 * General category "Ps" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte START_PUNCTUATION = 21;

	/**
	 * General category "Pe" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte END_PUNCTUATION = 22;

	/**
	 * General category "Pc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte CONNECTOR_PUNCTUATION = 23;

	/**
	 * General category "Po" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_PUNCTUATION = 24;

	/**
	 * General category "Sm" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte MATH_SYMBOL = 25;

	/**
	 * General category "Sc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte CURRENCY_SYMBOL = 26;

	/**
	 * General category "Sk" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte MODIFIER_SYMBOL = 27;

	/**
	 * General category "So" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_SYMBOL = 28;

	/**
	 * General category "Pi" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte INITIAL_QUOTE_PUNCTUATION = 29;

	/**
	 * General category "Pf" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte FINAL_QUOTE_PUNCTUATION = 30;

	/**
	 * Error flag. Use int (code point) to avoid confusion with U+FFFF.
	 */
	static final int ERROR = 0xFFFFFFFF;

	/**
	 * Undefined bidirectional character type. Undefined {@code char} values have undefined
	 * directionality in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_UNDEFINED = -1;

	/**
	 * Strong bidirectional character type "L" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;

	/**
	 * Strong bidirectional character type "R" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;

	/**
	 * Strong bidirectional character type "AL" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;

	/**
	 * Weak bidirectional character type "EN" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;

	/**
	 * Weak bidirectional character type "ES" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;

	/**
	 * Weak bidirectional character type "ET" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;

	/**
	 * Weak bidirectional character type "AN" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_ARABIC_NUMBER = 6;

	/**
	 * Weak bidirectional character type "CS" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;

	/**
	 * Weak bidirectional character type "NSM" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_NONSPACING_MARK = 8;

	/**
	 * Weak bidirectional character type "BN" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;

	/**
	 * Neutral bidirectional character type "B" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;

	/**
	 * Neutral bidirectional character type "S" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;

	/**
	 * Neutral bidirectional character type "WS" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_WHITESPACE = 12;

	/**
	 * Neutral bidirectional character type "ON" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13;

	/**
	 * Strong bidirectional character type "LRE" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;

	/**
	 * Strong bidirectional character type "LRO" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;

	/**
	 * Strong bidirectional character type "RLE" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;

	/**
	 * Strong bidirectional character type "RLO" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;

	/**
	 * Weak bidirectional character type "PDF" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;

	/**
	 * The minimum value of a <a href="http://www.unicode.org/glossary/#high_surrogate_code_unit">
	 * Unicode high-surrogate code unit</a> in the UTF-16 encoding, constant {@code '\u005CuD800'}.
	 * A high-surrogate is also known as a <i>leading-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MIN_HIGH_SURROGATE = '\uD800';

	/**
	 * The maximum value of a <a href="http://www.unicode.org/glossary/#high_surrogate_code_unit">
	 * Unicode high-surrogate code unit</a> in the UTF-16 encoding, constant {@code '\u005CuDBFF'}.
	 * A high-surrogate is also known as a <i>leading-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MAX_HIGH_SURROGATE = '\uDBFF';

	/**
	 * The minimum value of a <a href="http://www.unicode.org/glossary/#low_surrogate_code_unit">
	 * Unicode low-surrogate code unit</a> in the UTF-16 encoding, constant {@code '\u005CuDC00'}. A
	 * low-surrogate is also known as a <i>trailing-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MIN_LOW_SURROGATE = '\uDC00';

	/**
	 * The maximum value of a <a href="http://www.unicode.org/glossary/#low_surrogate_code_unit">
	 * Unicode low-surrogate code unit</a> in the UTF-16 encoding, constant {@code '\u005CuDFFF'}. A
	 * low-surrogate is also known as a <i>trailing-surrogate</i>.
	 *
	 * @since 1.5
	 */
	public static final char MAX_LOW_SURROGATE = '\uDFFF';

	/**
	 * The minimum value of a Unicode surrogate code unit in the UTF-16 encoding, constant
	 * {@code '\u005CuD800'}.
	 *
	 * @since 1.5
	 */
	public static final char MIN_SURROGATE = MIN_HIGH_SURROGATE;

	/**
	 * The maximum value of a Unicode surrogate code unit in the UTF-16 encoding, constant
	 * {@code '\u005CuDFFF'}.
	 *
	 * @since 1.5
	 */
	public static final char MAX_SURROGATE = MAX_LOW_SURROGATE;

	/**
	 * The minimum value of a <a href="http://www.unicode.org/glossary/#supplementary_code_point">
	 * Unicode supplementary code point</a>, constant {@code U+10000}.
	 *
	 * @since 1.5
	 */
	public static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;

	/**
	 * The minimum value of a <a href="http://www.unicode.org/glossary/#code_point"> Unicode code
	 * point</a>, constant {@code U+0000}.
	 *
	 * @since 1.5
	 */
	public static final int MIN_CODE_POINT = 0x000000;

	/**
	 * The maximum value of a <a href="http://www.unicode.org/glossary/#code_point"> Unicode code
	 * point</a>, constant {@code U+10FFFF}.
	 *
	 * @since 1.5
	 */
	public static final int MAX_CODE_POINT = 0X10FFFF;

	/**
	 * Instances of this class represent particular subsets of the Unicode character set. The only
	 * family of subsets defined in the {@code Character} class is {@link Character.UnicodeBlock}.
	 * Other portions of the Java API may define other subsets for their own purposes.
	 *
	 * @since 1.2
	 */
	public static class Subset {

		private String name;

		/**
		 * Constructs a new {@code Subset} instance.
		 *
		 * @param name The name of this subset
		 * @exception NullPointerException if name is {@code null}
		 */
		protected Subset(String name) {
			if (name == null) {
				throw new NullPointerException("name");
			}
			this.name = name;
		}

		/**
		 * Compares two {@code Subset} objects for equality. This method returns {@code true} if and
		 * only if {@code this} and the argument refer to the same object; since this method is
		 * {@code final}, this guarantee holds for all subclasses.
		 */
		public final boolean equals(Object obj) {
			return (this == obj);
		}

		/**
		 * Returns the standard hash code as defined by the {@link Object#hashCode} method. This
		 * method is {@code final} in order to ensure that the {@code equals} and {@code hashCode}
		 * methods will be consistent in all subclasses.
		 */
		public final int hashCode() {
			return super.hashCode();
		}

		/**
		 * Returns the name of this subset.
		 */
		public final String toString() {
			return name;
		}
	}

	// See http://www.unicode.org/Public/UNIDATA/Blocks.txt
	// for the latest specification of Unicode Blocks.

	/**
	 * A family of character subsets representing the character blocks in the Unicode specification.
	 * Character blocks generally define characters used for a specific script or purpose. A
	 * character is contained by at most one Unicode block.
	 *
	 * @since 1.2
	 */
	public static final class UnicodeBlock extends Subset {

		private static Map<String, UnicodeBlock> map = new HashMap<String, UnicodeBlock>(256);

		/**
		 * Creates a UnicodeBlock with the given identifier name. This name must be the same as the
		 * block identifier.
		 */
		private UnicodeBlock(String idName) {
			super(idName);
			map.put(idName, this);
		}

		/**
		 * Creates a UnicodeBlock with the given identifier name and alias name.
		 */
		private UnicodeBlock(String idName, String alias) {
			this(idName);
			map.put(alias, this);
		}

		/**
		 * Creates a UnicodeBlock with the given identifier name and alias names.
		 */
		private UnicodeBlock(String idName, String... aliases) {
			this(idName);
			for (String alias : aliases)
				map.put(alias, this);
		}

		/**
		 * Constant for the "Basic Latin" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock BASIC_LATIN = new UnicodeBlock("BASIC_LATIN", "BASIC LATIN",
				"BASICLATIN");

		/**
		 * Constant for the "Latin-1 Supplement" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock LATIN_1_SUPPLEMENT = new UnicodeBlock("LATIN_1_SUPPLEMENT",
				"LATIN-1 SUPPLEMENT", "LATIN-1SUPPLEMENT");

		/**
		 * Constant for the "Latin Extended-A" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock LATIN_EXTENDED_A = new UnicodeBlock("LATIN_EXTENDED_A",
				"LATIN EXTENDED-A", "LATINEXTENDED-A");

		/**
		 * Constant for the "Latin Extended-B" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock LATIN_EXTENDED_B = new UnicodeBlock("LATIN_EXTENDED_B",
				"LATIN EXTENDED-B", "LATINEXTENDED-B");

		/**
		 * Constant for the "IPA Extensions" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock IPA_EXTENSIONS = new UnicodeBlock("IPA_EXTENSIONS",
				"IPA EXTENSIONS", "IPAEXTENSIONS");

		/**
		 * Constant for the "Spacing Modifier Letters" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock SPACING_MODIFIER_LETTERS = new UnicodeBlock(
				"SPACING_MODIFIER_LETTERS", "SPACING MODIFIER LETTERS", "SPACINGMODIFIERLETTERS");

		/**
		 * Constant for the "Combining Diacritical Marks" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS = new UnicodeBlock(
				"COMBINING_DIACRITICAL_MARKS", "COMBINING DIACRITICAL MARKS", "COMBININGDIACRITICALMARKS");

		/**
		 * Constant for the "Greek and Coptic" Unicode character block.
		 * <p>
		 * This block was previously known as the "Greek" block.
		 *
		 * @since 1.2
		 */
		public static final UnicodeBlock GREEK = new UnicodeBlock("GREEK", "GREEK AND COPTIC",
				"GREEKANDCOPTIC");

		/**
		 * Constant for the "Cyrillic" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CYRILLIC = new UnicodeBlock("CYRILLIC");

		/**
		 * Constant for the "Armenian" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ARMENIAN = new UnicodeBlock("ARMENIAN");

		/**
		 * Constant for the "Hebrew" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock HEBREW = new UnicodeBlock("HEBREW");

		/**
		 * Constant for the "Arabic" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ARABIC = new UnicodeBlock("ARABIC");

		/**
		 * Constant for the "Devanagari" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock DEVANAGARI = new UnicodeBlock("DEVANAGARI");

		/**
		 * Constant for the "Bengali" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock BENGALI = new UnicodeBlock("BENGALI");

		/**
		 * Constant for the "Gurmukhi" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock GURMUKHI = new UnicodeBlock("GURMUKHI");

		/**
		 * Constant for the "Gujarati" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock GUJARATI = new UnicodeBlock("GUJARATI");

		/**
		 * Constant for the "Oriya" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ORIYA = new UnicodeBlock("ORIYA");

		/**
		 * Constant for the "Tamil" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock TAMIL = new UnicodeBlock("TAMIL");

		/**
		 * Constant for the "Telugu" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock TELUGU = new UnicodeBlock("TELUGU");

		/**
		 * Constant for the "Kannada" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock KANNADA = new UnicodeBlock("KANNADA");

		/**
		 * Constant for the "Malayalam" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock MALAYALAM = new UnicodeBlock("MALAYALAM");

		/**
		 * Constant for the "Thai" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock THAI = new UnicodeBlock("THAI");

		/**
		 * Constant for the "Lao" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock LAO = new UnicodeBlock("LAO");

		/**
		 * Constant for the "Tibetan" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock TIBETAN = new UnicodeBlock("TIBETAN");

		/**
		 * Constant for the "Georgian" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock GEORGIAN = new UnicodeBlock("GEORGIAN");

		/**
		 * Constant for the "Hangul Jamo" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock HANGUL_JAMO = new UnicodeBlock("HANGUL_JAMO", "HANGUL JAMO",
				"HANGULJAMO");

		/**
		 * Constant for the "Latin Extended Additional" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock LATIN_EXTENDED_ADDITIONAL = new UnicodeBlock(
				"LATIN_EXTENDED_ADDITIONAL", "LATIN EXTENDED ADDITIONAL", "LATINEXTENDEDADDITIONAL");

		/**
		 * Constant for the "Greek Extended" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock GREEK_EXTENDED = new UnicodeBlock("GREEK_EXTENDED",
				"GREEK EXTENDED", "GREEKEXTENDED");

		/**
		 * Constant for the "General Punctuation" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock GENERAL_PUNCTUATION = new UnicodeBlock("GENERAL_PUNCTUATION",
				"GENERAL PUNCTUATION", "GENERALPUNCTUATION");

		/**
		 * Constant for the "Superscripts and Subscripts" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS = new UnicodeBlock(
				"SUPERSCRIPTS_AND_SUBSCRIPTS", "SUPERSCRIPTS AND SUBSCRIPTS", "SUPERSCRIPTSANDSUBSCRIPTS");

		/**
		 * Constant for the "Currency Symbols" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CURRENCY_SYMBOLS = new UnicodeBlock("CURRENCY_SYMBOLS",
				"CURRENCY SYMBOLS", "CURRENCYSYMBOLS");

		/**
		 * Constant for the "Combining Diacritical Marks for Symbols" Unicode character block.
		 * <p>
		 * This block was previously known as "Combining Marks for Symbols".
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS = new UnicodeBlock(
				"COMBINING_MARKS_FOR_SYMBOLS", "COMBINING DIACRITICAL MARKS FOR SYMBOLS",
				"COMBININGDIACRITICALMARKSFORSYMBOLS", "COMBINING MARKS FOR SYMBOLS",
				"COMBININGMARKSFORSYMBOLS");

		/**
		 * Constant for the "Letterlike Symbols" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock LETTERLIKE_SYMBOLS = new UnicodeBlock("LETTERLIKE_SYMBOLS",
				"LETTERLIKE SYMBOLS", "LETTERLIKESYMBOLS");

		/**
		 * Constant for the "Number Forms" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock NUMBER_FORMS = new UnicodeBlock("NUMBER_FORMS", "NUMBER FORMS",
				"NUMBERFORMS");

		/**
		 * Constant for the "Arrows" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ARROWS = new UnicodeBlock("ARROWS");

		/**
		 * Constant for the "Mathematical Operators" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock MATHEMATICAL_OPERATORS = new UnicodeBlock("MATHEMATICAL_OPERATORS",
				"MATHEMATICAL OPERATORS", "MATHEMATICALOPERATORS");

		/**
		 * Constant for the "Miscellaneous Technical" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock MISCELLANEOUS_TECHNICAL = new UnicodeBlock(
				"MISCELLANEOUS_TECHNICAL", "MISCELLANEOUS TECHNICAL", "MISCELLANEOUSTECHNICAL");

		/**
		 * Constant for the "Control Pictures" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CONTROL_PICTURES = new UnicodeBlock("CONTROL_PICTURES",
				"CONTROL PICTURES", "CONTROLPICTURES");

		/**
		 * Constant for the "Optical Character Recognition" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock OPTICAL_CHARACTER_RECOGNITION = new UnicodeBlock(
				"OPTICAL_CHARACTER_RECOGNITION", "OPTICAL CHARACTER RECOGNITION",
				"OPTICALCHARACTERRECOGNITION");

		/**
		 * Constant for the "Enclosed Alphanumerics" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ENCLOSED_ALPHANUMERICS = new UnicodeBlock("ENCLOSED_ALPHANUMERICS",
				"ENCLOSED ALPHANUMERICS", "ENCLOSEDALPHANUMERICS");

		/**
		 * Constant for the "Box Drawing" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock BOX_DRAWING = new UnicodeBlock("BOX_DRAWING", "BOX DRAWING",
				"BOXDRAWING");

		/**
		 * Constant for the "Block Elements" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock BLOCK_ELEMENTS = new UnicodeBlock("BLOCK_ELEMENTS",
				"BLOCK ELEMENTS", "BLOCKELEMENTS");

		/**
		 * Constant for the "Geometric Shapes" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock GEOMETRIC_SHAPES = new UnicodeBlock("GEOMETRIC_SHAPES",
				"GEOMETRIC SHAPES", "GEOMETRICSHAPES");

		/**
		 * Constant for the "Miscellaneous Symbols" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock MISCELLANEOUS_SYMBOLS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS",
				"MISCELLANEOUS SYMBOLS", "MISCELLANEOUSSYMBOLS");

		/**
		 * Constant for the "Dingbats" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock DINGBATS = new UnicodeBlock("DINGBATS");

		/**
		 * Constant for the "CJK Symbols and Punctuation" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION = new UnicodeBlock(
				"CJK_SYMBOLS_AND_PUNCTUATION", "CJK SYMBOLS AND PUNCTUATION", "CJKSYMBOLSANDPUNCTUATION");

		/**
		 * Constant for the "Hiragana" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock HIRAGANA = new UnicodeBlock("HIRAGANA");

		/**
		 * Constant for the "Katakana" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock KATAKANA = new UnicodeBlock("KATAKANA");

		/**
		 * Constant for the "Bopomofo" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock BOPOMOFO = new UnicodeBlock("BOPOMOFO");

		/**
		 * Constant for the "Hangul Compatibility Jamo" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock HANGUL_COMPATIBILITY_JAMO = new UnicodeBlock(
				"HANGUL_COMPATIBILITY_JAMO", "HANGUL COMPATIBILITY JAMO", "HANGULCOMPATIBILITYJAMO");

		/**
		 * Constant for the "Kanbun" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock KANBUN = new UnicodeBlock("KANBUN");

		/**
		 * Constant for the "Enclosed CJK Letters and Months" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS = new UnicodeBlock(
				"ENCLOSED_CJK_LETTERS_AND_MONTHS", "ENCLOSED CJK LETTERS AND MONTHS",
				"ENCLOSEDCJKLETTERSANDMONTHS");

		/**
		 * Constant for the "CJK Compatibility" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CJK_COMPATIBILITY = new UnicodeBlock("CJK_COMPATIBILITY",
				"CJK COMPATIBILITY", "CJKCOMPATIBILITY");

		/**
		 * Constant for the "CJK Unified Ideographs" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS",
				"CJK UNIFIED IDEOGRAPHS", "CJKUNIFIEDIDEOGRAPHS");

		/**
		 * Constant for the "Hangul Syllables" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock HANGUL_SYLLABLES = new UnicodeBlock("HANGUL_SYLLABLES",
				"HANGUL SYLLABLES", "HANGULSYLLABLES");

		/**
		 * Constant for the "Private Use Area" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock PRIVATE_USE_AREA = new UnicodeBlock("PRIVATE_USE_AREA",
				"PRIVATE USE AREA", "PRIVATEUSEAREA");

		/**
		 * Constant for the "CJK Compatibility Ideographs" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS = new UnicodeBlock(
				"CJK_COMPATIBILITY_IDEOGRAPHS", "CJK COMPATIBILITY IDEOGRAPHS", "CJKCOMPATIBILITYIDEOGRAPHS");

		/**
		 * Constant for the "Alphabetic Presentation Forms" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ALPHABETIC_PRESENTATION_FORMS = new UnicodeBlock(
				"ALPHABETIC_PRESENTATION_FORMS", "ALPHABETIC PRESENTATION FORMS",
				"ALPHABETICPRESENTATIONFORMS");

		/**
		 * Constant for the "Arabic Presentation Forms-A" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_A = new UnicodeBlock(
				"ARABIC_PRESENTATION_FORMS_A", "ARABIC PRESENTATION FORMS-A", "ARABICPRESENTATIONFORMS-A");

		/**
		 * Constant for the "Combining Half Marks" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock COMBINING_HALF_MARKS = new UnicodeBlock("COMBINING_HALF_MARKS",
				"COMBINING HALF MARKS", "COMBININGHALFMARKS");

		/**
		 * Constant for the "CJK Compatibility Forms" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock CJK_COMPATIBILITY_FORMS = new UnicodeBlock(
				"CJK_COMPATIBILITY_FORMS", "CJK COMPATIBILITY FORMS", "CJKCOMPATIBILITYFORMS");

		/**
		 * Constant for the "Small Form Variants" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock SMALL_FORM_VARIANTS = new UnicodeBlock("SMALL_FORM_VARIANTS",
				"SMALL FORM VARIANTS", "SMALLFORMVARIANTS");

		/**
		 * Constant for the "Arabic Presentation Forms-B" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_B = new UnicodeBlock(
				"ARABIC_PRESENTATION_FORMS_B", "ARABIC PRESENTATION FORMS-B", "ARABICPRESENTATIONFORMS-B");

		/**
		 * Constant for the "Halfwidth and Fullwidth Forms" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS = new UnicodeBlock(
				"HALFWIDTH_AND_FULLWIDTH_FORMS", "HALFWIDTH AND FULLWIDTH FORMS",
				"HALFWIDTHANDFULLWIDTHFORMS");

		/**
		 * Constant for the "Specials" Unicode character block.
		 * 
		 * @since 1.2
		 */
		public static final UnicodeBlock SPECIALS = new UnicodeBlock("SPECIALS");

		/**
		 * @deprecated As of J2SE 5, use {@link #HIGH_SURROGATES},
		 *             {@link #HIGH_PRIVATE_USE_SURROGATES}, and {@link #LOW_SURROGATES}. These new
		 *             constants match the block definitions of the Unicode Standard. The
		 *             {@link #of(char)} and {@link #of(int)} methods return the new constants, not
		 *             SURROGATES_AREA.
		 */
		@Deprecated
		public static final UnicodeBlock SURROGATES_AREA = new UnicodeBlock("SURROGATES_AREA");

		/**
		 * Constant for the "Syriac" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock SYRIAC = new UnicodeBlock("SYRIAC");

		/**
		 * Constant for the "Thaana" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock THAANA = new UnicodeBlock("THAANA");

		/**
		 * Constant for the "Sinhala" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock SINHALA = new UnicodeBlock("SINHALA");

		/**
		 * Constant for the "Myanmar" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock MYANMAR = new UnicodeBlock("MYANMAR");

		/**
		 * Constant for the "Ethiopic" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock ETHIOPIC = new UnicodeBlock("ETHIOPIC");

		/**
		 * Constant for the "Cherokee" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock CHEROKEE = new UnicodeBlock("CHEROKEE");

		/**
		 * Constant for the "Unified Canadian Aboriginal Syllabics" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = new UnicodeBlock(
				"UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS", "UNIFIED CANADIAN ABORIGINAL SYLLABICS",
				"UNIFIEDCANADIANABORIGINALSYLLABICS");

		/**
		 * Constant for the "Ogham" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock OGHAM = new UnicodeBlock("OGHAM");

		/**
		 * Constant for the "Runic" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock RUNIC = new UnicodeBlock("RUNIC");

		/**
		 * Constant for the "Khmer" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock KHMER = new UnicodeBlock("KHMER");

		/**
		 * Constant for the "Mongolian" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock MONGOLIAN = new UnicodeBlock("MONGOLIAN");

		/**
		 * Constant for the "Braille Patterns" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock BRAILLE_PATTERNS = new UnicodeBlock("BRAILLE_PATTERNS",
				"BRAILLE PATTERNS", "BRAILLEPATTERNS");

		/**
		 * Constant for the "CJK Radicals Supplement" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock CJK_RADICALS_SUPPLEMENT = new UnicodeBlock(
				"CJK_RADICALS_SUPPLEMENT", "CJK RADICALS SUPPLEMENT", "CJKRADICALSSUPPLEMENT");

		/**
		 * Constant for the "Kangxi Radicals" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock KANGXI_RADICALS = new UnicodeBlock("KANGXI_RADICALS",
				"KANGXI RADICALS", "KANGXIRADICALS");

		/**
		 * Constant for the "Ideographic Description Characters" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS = new UnicodeBlock(
				"IDEOGRAPHIC_DESCRIPTION_CHARACTERS", "IDEOGRAPHIC DESCRIPTION CHARACTERS",
				"IDEOGRAPHICDESCRIPTIONCHARACTERS");

		/**
		 * Constant for the "Bopomofo Extended" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock BOPOMOFO_EXTENDED = new UnicodeBlock("BOPOMOFO_EXTENDED",
				"BOPOMOFO EXTENDED", "BOPOMOFOEXTENDED");

		/**
		 * Constant for the "CJK Unified Ideographs Extension A" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A = new UnicodeBlock(
				"CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A", "CJK UNIFIED IDEOGRAPHS EXTENSION A",
				"CJKUNIFIEDIDEOGRAPHSEXTENSIONA");

		/**
		 * Constant for the "Yi Syllables" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock YI_SYLLABLES = new UnicodeBlock("YI_SYLLABLES", "YI SYLLABLES",
				"YISYLLABLES");

		/**
		 * Constant for the "Yi Radicals" Unicode character block.
		 * 
		 * @since 1.4
		 */
		public static final UnicodeBlock YI_RADICALS = new UnicodeBlock("YI_RADICALS", "YI RADICALS",
				"YIRADICALS");

		/**
		 * Constant for the "Cyrillic Supplementary" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock CYRILLIC_SUPPLEMENTARY = new UnicodeBlock("CYRILLIC_SUPPLEMENTARY",
				"CYRILLIC SUPPLEMENTARY", "CYRILLICSUPPLEMENTARY", "CYRILLIC SUPPLEMENT",
				"CYRILLICSUPPLEMENT");

		/**
		 * Constant for the "Tagalog" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock TAGALOG = new UnicodeBlock("TAGALOG");

		/**
		 * Constant for the "Hanunoo" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock HANUNOO = new UnicodeBlock("HANUNOO");

		/**
		 * Constant for the "Buhid" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock BUHID = new UnicodeBlock("BUHID");

		/**
		 * Constant for the "Tagbanwa" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock TAGBANWA = new UnicodeBlock("TAGBANWA");

		/**
		 * Constant for the "Limbu" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock LIMBU = new UnicodeBlock("LIMBU");

		/**
		 * Constant for the "Tai Le" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock TAI_LE = new UnicodeBlock("TAI_LE", "TAI LE", "TAILE");

		/**
		 * Constant for the "Khmer Symbols" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock KHMER_SYMBOLS = new UnicodeBlock("KHMER_SYMBOLS", "KHMER SYMBOLS",
				"KHMERSYMBOLS");

		/**
		 * Constant for the "Phonetic Extensions" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock PHONETIC_EXTENSIONS = new UnicodeBlock("PHONETIC_EXTENSIONS",
				"PHONETIC EXTENSIONS", "PHONETICEXTENSIONS");

		/**
		 * Constant for the "Miscellaneous Mathematical Symbols-A" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A = new UnicodeBlock(
				"MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A", "MISCELLANEOUS MATHEMATICAL SYMBOLS-A",
				"MISCELLANEOUSMATHEMATICALSYMBOLS-A");

		/**
		 * Constant for the "Supplemental Arrows-A" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock SUPPLEMENTAL_ARROWS_A = new UnicodeBlock("SUPPLEMENTAL_ARROWS_A",
				"SUPPLEMENTAL ARROWS-A", "SUPPLEMENTALARROWS-A");

		/**
		 * Constant for the "Supplemental Arrows-B" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock SUPPLEMENTAL_ARROWS_B = new UnicodeBlock("SUPPLEMENTAL_ARROWS_B",
				"SUPPLEMENTAL ARROWS-B", "SUPPLEMENTALARROWS-B");

		/**
		 * Constant for the "Miscellaneous Mathematical Symbols-B" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B = new UnicodeBlock(
				"MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B", "MISCELLANEOUS MATHEMATICAL SYMBOLS-B",
				"MISCELLANEOUSMATHEMATICALSYMBOLS-B");

		/**
		 * Constant for the "Supplemental Mathematical Operators" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS = new UnicodeBlock(
				"SUPPLEMENTAL_MATHEMATICAL_OPERATORS", "SUPPLEMENTAL MATHEMATICAL OPERATORS",
				"SUPPLEMENTALMATHEMATICALOPERATORS");

		/**
		 * Constant for the "Miscellaneous Symbols and Arrows" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS = new UnicodeBlock(
				"MISCELLANEOUS_SYMBOLS_AND_ARROWS", "MISCELLANEOUS SYMBOLS AND ARROWS",
				"MISCELLANEOUSSYMBOLSANDARROWS");

		/**
		 * Constant for the "Katakana Phonetic Extensions" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS = new UnicodeBlock(
				"KATAKANA_PHONETIC_EXTENSIONS", "KATAKANA PHONETIC EXTENSIONS", "KATAKANAPHONETICEXTENSIONS");

		/**
		 * Constant for the "Yijing Hexagram Symbols" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock YIJING_HEXAGRAM_SYMBOLS = new UnicodeBlock(
				"YIJING_HEXAGRAM_SYMBOLS", "YIJING HEXAGRAM SYMBOLS", "YIJINGHEXAGRAMSYMBOLS");

		/**
		 * Constant for the "Variation Selectors" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock VARIATION_SELECTORS = new UnicodeBlock("VARIATION_SELECTORS",
				"VARIATION SELECTORS", "VARIATIONSELECTORS");

		/**
		 * Constant for the "Linear B Syllabary" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock LINEAR_B_SYLLABARY = new UnicodeBlock("LINEAR_B_SYLLABARY",
				"LINEAR B SYLLABARY", "LINEARBSYLLABARY");

		/**
		 * Constant for the "Linear B Ideograms" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock LINEAR_B_IDEOGRAMS = new UnicodeBlock("LINEAR_B_IDEOGRAMS",
				"LINEAR B IDEOGRAMS", "LINEARBIDEOGRAMS");

		/**
		 * Constant for the "Aegean Numbers" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock AEGEAN_NUMBERS = new UnicodeBlock("AEGEAN_NUMBERS",
				"AEGEAN NUMBERS", "AEGEANNUMBERS");

		/**
		 * Constant for the "Old Italic" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock OLD_ITALIC = new UnicodeBlock("OLD_ITALIC", "OLD ITALIC",
				"OLDITALIC");

		/**
		 * Constant for the "Gothic" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock GOTHIC = new UnicodeBlock("GOTHIC");

		/**
		 * Constant for the "Ugaritic" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock UGARITIC = new UnicodeBlock("UGARITIC");

		/**
		 * Constant for the "Deseret" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock DESERET = new UnicodeBlock("DESERET");

		/**
		 * Constant for the "Shavian" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock SHAVIAN = new UnicodeBlock("SHAVIAN");

		/**
		 * Constant for the "Osmanya" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock OSMANYA = new UnicodeBlock("OSMANYA");

		/**
		 * Constant for the "Cypriot Syllabary" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock CYPRIOT_SYLLABARY = new UnicodeBlock("CYPRIOT_SYLLABARY",
				"CYPRIOT SYLLABARY", "CYPRIOTSYLLABARY");

		/**
		 * Constant for the "Byzantine Musical Symbols" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS = new UnicodeBlock(
				"BYZANTINE_MUSICAL_SYMBOLS", "BYZANTINE MUSICAL SYMBOLS", "BYZANTINEMUSICALSYMBOLS");

		/**
		 * Constant for the "Musical Symbols" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock MUSICAL_SYMBOLS = new UnicodeBlock("MUSICAL_SYMBOLS",
				"MUSICAL SYMBOLS", "MUSICALSYMBOLS");

		/**
		 * Constant for the "Tai Xuan Jing Symbols" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock TAI_XUAN_JING_SYMBOLS = new UnicodeBlock("TAI_XUAN_JING_SYMBOLS",
				"TAI XUAN JING SYMBOLS", "TAIXUANJINGSYMBOLS");

		/**
		 * Constant for the "Mathematical Alphanumeric Symbols" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS = new UnicodeBlock(
				"MATHEMATICAL_ALPHANUMERIC_SYMBOLS", "MATHEMATICAL ALPHANUMERIC SYMBOLS",
				"MATHEMATICALALPHANUMERICSYMBOLS");

		/**
		 * Constant for the "CJK Unified Ideographs Extension B" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B = new UnicodeBlock(
				"CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B", "CJK UNIFIED IDEOGRAPHS EXTENSION B",
				"CJKUNIFIEDIDEOGRAPHSEXTENSIONB");

		/**
		 * Constant for the "CJK Compatibility Ideographs Supplement" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT = new UnicodeBlock(
				"CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT", "CJK COMPATIBILITY IDEOGRAPHS SUPPLEMENT",
				"CJKCOMPATIBILITYIDEOGRAPHSSUPPLEMENT");

		/**
		 * Constant for the "Tags" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock TAGS = new UnicodeBlock("TAGS");

		/**
		 * Constant for the "Variation Selectors Supplement" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT = new UnicodeBlock(
				"VARIATION_SELECTORS_SUPPLEMENT", "VARIATION SELECTORS SUPPLEMENT",
				"VARIATIONSELECTORSSUPPLEMENT");

		/**
		 * Constant for the "Supplementary Private Use Area-A" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A = new UnicodeBlock(
				"SUPPLEMENTARY_PRIVATE_USE_AREA_A", "SUPPLEMENTARY PRIVATE USE AREA-A",
				"SUPPLEMENTARYPRIVATEUSEAREA-A");

		/**
		 * Constant for the "Supplementary Private Use Area-B" Unicode character block.
		 * 
		 * @since 1.5
		 */
		public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B = new UnicodeBlock(
				"SUPPLEMENTARY_PRIVATE_USE_AREA_B", "SUPPLEMENTARY PRIVATE USE AREA-B",
				"SUPPLEMENTARYPRIVATEUSEAREA-B");

		/**
		 * Constant for the "High Surrogates" Unicode character block. This block represents
		 * codepoint values in the high surrogate range: U+D800 through U+DB7F
		 *
		 * @since 1.5
		 */
		public static final UnicodeBlock HIGH_SURROGATES = new UnicodeBlock("HIGH_SURROGATES",
				"HIGH SURROGATES", "HIGHSURROGATES");

		/**
		 * Constant for the "High Private Use Surrogates" Unicode character block. This block
		 * represents codepoint values in the private use high surrogate range: U+DB80 through
		 * U+DBFF
		 *
		 * @since 1.5
		 */
		public static final UnicodeBlock HIGH_PRIVATE_USE_SURROGATES = new UnicodeBlock(
				"HIGH_PRIVATE_USE_SURROGATES", "HIGH PRIVATE USE SURROGATES", "HIGHPRIVATEUSESURROGATES");

		/**
		 * Constant for the "Low Surrogates" Unicode character block. This block represents
		 * codepoint values in the low surrogate range: U+DC00 through U+DFFF
		 *
		 * @since 1.5
		 */
		public static final UnicodeBlock LOW_SURROGATES = new UnicodeBlock("LOW_SURROGATES",
				"LOW SURROGATES", "LOWSURROGATES");

		/**
		 * Constant for the "Arabic Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ARABIC_SUPPLEMENT = new UnicodeBlock("ARABIC_SUPPLEMENT",
				"ARABIC SUPPLEMENT", "ARABICSUPPLEMENT");

		/**
		 * Constant for the "NKo" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock NKO = new UnicodeBlock("NKO");

		/**
		 * Constant for the "Samaritan" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock SAMARITAN = new UnicodeBlock("SAMARITAN");

		/**
		 * Constant for the "Mandaic" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock MANDAIC = new UnicodeBlock("MANDAIC");

		/**
		 * Constant for the "Ethiopic Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ETHIOPIC_SUPPLEMENT = new UnicodeBlock("ETHIOPIC_SUPPLEMENT",
				"ETHIOPIC SUPPLEMENT", "ETHIOPICSUPPLEMENT");

		/**
		 * Constant for the "Unified Canadian Aboriginal Syllabics Extended" Unicode character
		 * block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED = new UnicodeBlock(
				"UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED",
				"UNIFIED CANADIAN ABORIGINAL SYLLABICS EXTENDED",
				"UNIFIEDCANADIANABORIGINALSYLLABICSEXTENDED");

		/**
		 * Constant for the "New Tai Lue" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock NEW_TAI_LUE = new UnicodeBlock("NEW_TAI_LUE", "NEW TAI LUE",
				"NEWTAILUE");

		/**
		 * Constant for the "Buginese" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock BUGINESE = new UnicodeBlock("BUGINESE");

		/**
		 * Constant for the "Tai Tham" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock TAI_THAM = new UnicodeBlock("TAI_THAM", "TAI THAM", "TAITHAM");

		/**
		 * Constant for the "Balinese" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock BALINESE = new UnicodeBlock("BALINESE");

		/**
		 * Constant for the "Sundanese" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock SUNDANESE = new UnicodeBlock("SUNDANESE");

		/**
		 * Constant for the "Batak" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock BATAK = new UnicodeBlock("BATAK");

		/**
		 * Constant for the "Lepcha" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock LEPCHA = new UnicodeBlock("LEPCHA");

		/**
		 * Constant for the "Ol Chiki" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock OL_CHIKI = new UnicodeBlock("OL_CHIKI", "OL CHIKI", "OLCHIKI");

		/**
		 * Constant for the "Vedic Extensions" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock VEDIC_EXTENSIONS = new UnicodeBlock("VEDIC_EXTENSIONS",
				"VEDIC EXTENSIONS", "VEDICEXTENSIONS");

		/**
		 * Constant for the "Phonetic Extensions Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT = new UnicodeBlock(
				"PHONETIC_EXTENSIONS_SUPPLEMENT", "PHONETIC EXTENSIONS SUPPLEMENT",
				"PHONETICEXTENSIONSSUPPLEMENT");

		/**
		 * Constant for the "Combining Diacritical Marks Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT = new UnicodeBlock(
				"COMBINING_DIACRITICAL_MARKS_SUPPLEMENT", "COMBINING DIACRITICAL MARKS SUPPLEMENT",
				"COMBININGDIACRITICALMARKSSUPPLEMENT");

		/**
		 * Constant for the "Glagolitic" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock GLAGOLITIC = new UnicodeBlock("GLAGOLITIC");

		/**
		 * Constant for the "Latin Extended-C" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock LATIN_EXTENDED_C = new UnicodeBlock("LATIN_EXTENDED_C",
				"LATIN EXTENDED-C", "LATINEXTENDED-C");

		/**
		 * Constant for the "Coptic" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock COPTIC = new UnicodeBlock("COPTIC");

		/**
		 * Constant for the "Georgian Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock GEORGIAN_SUPPLEMENT = new UnicodeBlock("GEORGIAN_SUPPLEMENT",
				"GEORGIAN SUPPLEMENT", "GEORGIANSUPPLEMENT");

		/**
		 * Constant for the "Tifinagh" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock TIFINAGH = new UnicodeBlock("TIFINAGH");

		/**
		 * Constant for the "Ethiopic Extended" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ETHIOPIC_EXTENDED = new UnicodeBlock("ETHIOPIC_EXTENDED",
				"ETHIOPIC EXTENDED", "ETHIOPICEXTENDED");

		/**
		 * Constant for the "Cyrillic Extended-A" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CYRILLIC_EXTENDED_A = new UnicodeBlock("CYRILLIC_EXTENDED_A",
				"CYRILLIC EXTENDED-A", "CYRILLICEXTENDED-A");

		/**
		 * Constant for the "Supplemental Punctuation" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock SUPPLEMENTAL_PUNCTUATION = new UnicodeBlock(
				"SUPPLEMENTAL_PUNCTUATION", "SUPPLEMENTAL PUNCTUATION", "SUPPLEMENTALPUNCTUATION");

		/**
		 * Constant for the "CJK Strokes" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CJK_STROKES = new UnicodeBlock("CJK_STROKES", "CJK STROKES",
				"CJKSTROKES");

		/**
		 * Constant for the "Lisu" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock LISU = new UnicodeBlock("LISU");

		/**
		 * Constant for the "Vai" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock VAI = new UnicodeBlock("VAI");

		/**
		 * Constant for the "Cyrillic Extended-B" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CYRILLIC_EXTENDED_B = new UnicodeBlock("CYRILLIC_EXTENDED_B",
				"CYRILLIC EXTENDED-B", "CYRILLICEXTENDED-B");

		/**
		 * Constant for the "Bamum" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock BAMUM = new UnicodeBlock("BAMUM");

		/**
		 * Constant for the "Modifier Tone Letters" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock MODIFIER_TONE_LETTERS = new UnicodeBlock("MODIFIER_TONE_LETTERS",
				"MODIFIER TONE LETTERS", "MODIFIERTONELETTERS");

		/**
		 * Constant for the "Latin Extended-D" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock LATIN_EXTENDED_D = new UnicodeBlock("LATIN_EXTENDED_D",
				"LATIN EXTENDED-D", "LATINEXTENDED-D");

		/**
		 * Constant for the "Syloti Nagri" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock SYLOTI_NAGRI = new UnicodeBlock("SYLOTI_NAGRI", "SYLOTI NAGRI",
				"SYLOTINAGRI");

		/**
		 * Constant for the "Common Indic Number Forms" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock COMMON_INDIC_NUMBER_FORMS = new UnicodeBlock(
				"COMMON_INDIC_NUMBER_FORMS", "COMMON INDIC NUMBER FORMS", "COMMONINDICNUMBERFORMS");

		/**
		 * Constant for the "Phags-pa" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock PHAGS_PA = new UnicodeBlock("PHAGS_PA", "PHAGS-PA");

		/**
		 * Constant for the "Saurashtra" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock SAURASHTRA = new UnicodeBlock("SAURASHTRA");

		/**
		 * Constant for the "Devanagari Extended" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock DEVANAGARI_EXTENDED = new UnicodeBlock("DEVANAGARI_EXTENDED",
				"DEVANAGARI EXTENDED", "DEVANAGARIEXTENDED");

		/**
		 * Constant for the "Kayah Li" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock KAYAH_LI = new UnicodeBlock("KAYAH_LI", "KAYAH LI", "KAYAHLI");

		/**
		 * Constant for the "Rejang" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock REJANG = new UnicodeBlock("REJANG");

		/**
		 * Constant for the "Hangul Jamo Extended-A" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock HANGUL_JAMO_EXTENDED_A = new UnicodeBlock("HANGUL_JAMO_EXTENDED_A",
				"HANGUL JAMO EXTENDED-A", "HANGULJAMOEXTENDED-A");

		/**
		 * Constant for the "Javanese" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock JAVANESE = new UnicodeBlock("JAVANESE");

		/**
		 * Constant for the "Cham" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CHAM = new UnicodeBlock("CHAM");

		/**
		 * Constant for the "Myanmar Extended-A" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock MYANMAR_EXTENDED_A = new UnicodeBlock("MYANMAR_EXTENDED_A",
				"MYANMAR EXTENDED-A", "MYANMAREXTENDED-A");

		/**
		 * Constant for the "Tai Viet" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock TAI_VIET = new UnicodeBlock("TAI_VIET", "TAI VIET", "TAIVIET");

		/**
		 * Constant for the "Ethiopic Extended-A" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ETHIOPIC_EXTENDED_A = new UnicodeBlock("ETHIOPIC_EXTENDED_A",
				"ETHIOPIC EXTENDED-A", "ETHIOPICEXTENDED-A");

		/**
		 * Constant for the "Meetei Mayek" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock MEETEI_MAYEK = new UnicodeBlock("MEETEI_MAYEK", "MEETEI MAYEK",
				"MEETEIMAYEK");

		/**
		 * Constant for the "Hangul Jamo Extended-B" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock HANGUL_JAMO_EXTENDED_B = new UnicodeBlock("HANGUL_JAMO_EXTENDED_B",
				"HANGUL JAMO EXTENDED-B", "HANGULJAMOEXTENDED-B");

		/**
		 * Constant for the "Vertical Forms" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock VERTICAL_FORMS = new UnicodeBlock("VERTICAL_FORMS",
				"VERTICAL FORMS", "VERTICALFORMS");

		/**
		 * Constant for the "Ancient Greek Numbers" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ANCIENT_GREEK_NUMBERS = new UnicodeBlock("ANCIENT_GREEK_NUMBERS",
				"ANCIENT GREEK NUMBERS", "ANCIENTGREEKNUMBERS");

		/**
		 * Constant for the "Ancient Symbols" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ANCIENT_SYMBOLS = new UnicodeBlock("ANCIENT_SYMBOLS",
				"ANCIENT SYMBOLS", "ANCIENTSYMBOLS");

		/**
		 * Constant for the "Phaistos Disc" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock PHAISTOS_DISC = new UnicodeBlock("PHAISTOS_DISC", "PHAISTOS DISC",
				"PHAISTOSDISC");

		/**
		 * Constant for the "Lycian" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock LYCIAN = new UnicodeBlock("LYCIAN");

		/**
		 * Constant for the "Carian" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CARIAN = new UnicodeBlock("CARIAN");

		/**
		 * Constant for the "Old Persian" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock OLD_PERSIAN = new UnicodeBlock("OLD_PERSIAN", "OLD PERSIAN",
				"OLDPERSIAN");

		/**
		 * Constant for the "Imperial Aramaic" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock IMPERIAL_ARAMAIC = new UnicodeBlock("IMPERIAL_ARAMAIC",
				"IMPERIAL ARAMAIC", "IMPERIALARAMAIC");

		/**
		 * Constant for the "Phoenician" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock PHOENICIAN = new UnicodeBlock("PHOENICIAN");

		/**
		 * Constant for the "Lydian" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock LYDIAN = new UnicodeBlock("LYDIAN");

		/**
		 * Constant for the "Kharoshthi" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock KHAROSHTHI = new UnicodeBlock("KHAROSHTHI");

		/**
		 * Constant for the "Old South Arabian" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock OLD_SOUTH_ARABIAN = new UnicodeBlock("OLD_SOUTH_ARABIAN",
				"OLD SOUTH ARABIAN", "OLDSOUTHARABIAN");

		/**
		 * Constant for the "Avestan" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock AVESTAN = new UnicodeBlock("AVESTAN");

		/**
		 * Constant for the "Inscriptional Parthian" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock INSCRIPTIONAL_PARTHIAN = new UnicodeBlock("INSCRIPTIONAL_PARTHIAN",
				"INSCRIPTIONAL PARTHIAN", "INSCRIPTIONALPARTHIAN");

		/**
		 * Constant for the "Inscriptional Pahlavi" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock INSCRIPTIONAL_PAHLAVI = new UnicodeBlock("INSCRIPTIONAL_PAHLAVI",
				"INSCRIPTIONAL PAHLAVI", "INSCRIPTIONALPAHLAVI");

		/**
		 * Constant for the "Old Turkic" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock OLD_TURKIC = new UnicodeBlock("OLD_TURKIC", "OLD TURKIC",
				"OLDTURKIC");

		/**
		 * Constant for the "Rumi Numeral Symbols" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock RUMI_NUMERAL_SYMBOLS = new UnicodeBlock("RUMI_NUMERAL_SYMBOLS",
				"RUMI NUMERAL SYMBOLS", "RUMINUMERALSYMBOLS");

		/**
		 * Constant for the "Brahmi" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock BRAHMI = new UnicodeBlock("BRAHMI");

		/**
		 * Constant for the "Kaithi" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock KAITHI = new UnicodeBlock("KAITHI");

		/**
		 * Constant for the "Cuneiform" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CUNEIFORM = new UnicodeBlock("CUNEIFORM");

		/**
		 * Constant for the "Cuneiform Numbers and Punctuation" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CUNEIFORM_NUMBERS_AND_PUNCTUATION = new UnicodeBlock(
				"CUNEIFORM_NUMBERS_AND_PUNCTUATION", "CUNEIFORM NUMBERS AND PUNCTUATION",
				"CUNEIFORMNUMBERSANDPUNCTUATION");

		/**
		 * Constant for the "Egyptian Hieroglyphs" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock EGYPTIAN_HIEROGLYPHS = new UnicodeBlock("EGYPTIAN_HIEROGLYPHS",
				"EGYPTIAN HIEROGLYPHS", "EGYPTIANHIEROGLYPHS");

		/**
		 * Constant for the "Bamum Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock BAMUM_SUPPLEMENT = new UnicodeBlock("BAMUM_SUPPLEMENT",
				"BAMUM SUPPLEMENT", "BAMUMSUPPLEMENT");

		/**
		 * Constant for the "Kana Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock KANA_SUPPLEMENT = new UnicodeBlock("KANA_SUPPLEMENT",
				"KANA SUPPLEMENT", "KANASUPPLEMENT");

		/**
		 * Constant for the "Ancient Greek Musical Notation" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION = new UnicodeBlock(
				"ANCIENT_GREEK_MUSICAL_NOTATION", "ANCIENT GREEK MUSICAL NOTATION",
				"ANCIENTGREEKMUSICALNOTATION");

		/**
		 * Constant for the "Counting Rod Numerals" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock COUNTING_ROD_NUMERALS = new UnicodeBlock("COUNTING_ROD_NUMERALS",
				"COUNTING ROD NUMERALS", "COUNTINGRODNUMERALS");

		/**
		 * Constant for the "Mahjong Tiles" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock MAHJONG_TILES = new UnicodeBlock("MAHJONG_TILES", "MAHJONG TILES",
				"MAHJONGTILES");

		/**
		 * Constant for the "Domino Tiles" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock DOMINO_TILES = new UnicodeBlock("DOMINO_TILES", "DOMINO TILES",
				"DOMINOTILES");

		/**
		 * Constant for the "Playing Cards" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock PLAYING_CARDS = new UnicodeBlock("PLAYING_CARDS", "PLAYING CARDS",
				"PLAYINGCARDS");

		/**
		 * Constant for the "Enclosed Alphanumeric Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ENCLOSED_ALPHANUMERIC_SUPPLEMENT = new UnicodeBlock(
				"ENCLOSED_ALPHANUMERIC_SUPPLEMENT", "ENCLOSED ALPHANUMERIC SUPPLEMENT",
				"ENCLOSEDALPHANUMERICSUPPLEMENT");

		/**
		 * Constant for the "Enclosed Ideographic Supplement" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ENCLOSED_IDEOGRAPHIC_SUPPLEMENT = new UnicodeBlock(
				"ENCLOSED_IDEOGRAPHIC_SUPPLEMENT", "ENCLOSED IDEOGRAPHIC SUPPLEMENT",
				"ENCLOSEDIDEOGRAPHICSUPPLEMENT");

		/**
		 * Constant for the "Miscellaneous Symbols And Pictographs" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS = new UnicodeBlock(
				"MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS", "MISCELLANEOUS SYMBOLS AND PICTOGRAPHS",
				"MISCELLANEOUSSYMBOLSANDPICTOGRAPHS");

		/**
		 * Constant for the "Emoticons" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock EMOTICONS = new UnicodeBlock("EMOTICONS");

		/**
		 * Constant for the "Transport And Map Symbols" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock TRANSPORT_AND_MAP_SYMBOLS = new UnicodeBlock(
				"TRANSPORT_AND_MAP_SYMBOLS", "TRANSPORT AND MAP SYMBOLS", "TRANSPORTANDMAPSYMBOLS");

		/**
		 * Constant for the "Alchemical Symbols" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock ALCHEMICAL_SYMBOLS = new UnicodeBlock("ALCHEMICAL_SYMBOLS",
				"ALCHEMICAL SYMBOLS", "ALCHEMICALSYMBOLS");

		/**
		 * Constant for the "CJK Unified Ideographs Extension C" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C = new UnicodeBlock(
				"CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C", "CJK UNIFIED IDEOGRAPHS EXTENSION C",
				"CJKUNIFIEDIDEOGRAPHSEXTENSIONC");

		/**
		 * Constant for the "CJK Unified Ideographs Extension D" Unicode character block.
		 * 
		 * @since 1.7
		 */
		public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D = new UnicodeBlock(
				"CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D", "CJK UNIFIED IDEOGRAPHS EXTENSION D",
				"CJKUNIFIEDIDEOGRAPHSEXTENSIOND");

		private static final int blockStarts[] = { 0x0000, // 0000..007F; Basic Latin
				0x0080, // 0080..00FF; Latin-1 Supplement
				0x0100, // 0100..017F; Latin Extended-A
				0x0180, // 0180..024F; Latin Extended-B
				0x0250, // 0250..02AF; IPA Extensions
				0x02B0, // 02B0..02FF; Spacing Modifier Letters
				0x0300, // 0300..036F; Combining Diacritical Marks
				0x0370, // 0370..03FF; Greek and Coptic
				0x0400, // 0400..04FF; Cyrillic
				0x0500, // 0500..052F; Cyrillic Supplement
				0x0530, // 0530..058F; Armenian
				0x0590, // 0590..05FF; Hebrew
				0x0600, // 0600..06FF; Arabic
				0x0700, // 0700..074F; Syriac
				0x0750, // 0750..077F; Arabic Supplement
				0x0780, // 0780..07BF; Thaana
				0x07C0, // 07C0..07FF; NKo
				0x0800, // 0800..083F; Samaritan
				0x0840, // 0840..085F; Mandaic
				0x0860, // unassigned
				0x0900, // 0900..097F; Devanagari
				0x0980, // 0980..09FF; Bengali
				0x0A00, // 0A00..0A7F; Gurmukhi
				0x0A80, // 0A80..0AFF; Gujarati
				0x0B00, // 0B00..0B7F; Oriya
				0x0B80, // 0B80..0BFF; Tamil
				0x0C00, // 0C00..0C7F; Telugu
				0x0C80, // 0C80..0CFF; Kannada
				0x0D00, // 0D00..0D7F; Malayalam
				0x0D80, // 0D80..0DFF; Sinhala
				0x0E00, // 0E00..0E7F; Thai
				0x0E80, // 0E80..0EFF; Lao
				0x0F00, // 0F00..0FFF; Tibetan
				0x1000, // 1000..109F; Myanmar
				0x10A0, // 10A0..10FF; Georgian
				0x1100, // 1100..11FF; Hangul Jamo
				0x1200, // 1200..137F; Ethiopic
				0x1380, // 1380..139F; Ethiopic Supplement
				0x13A0, // 13A0..13FF; Cherokee
				0x1400, // 1400..167F; Unified Canadian Aboriginal Syllabics
				0x1680, // 1680..169F; Ogham
				0x16A0, // 16A0..16FF; Runic
				0x1700, // 1700..171F; Tagalog
				0x1720, // 1720..173F; Hanunoo
				0x1740, // 1740..175F; Buhid
				0x1760, // 1760..177F; Tagbanwa
				0x1780, // 1780..17FF; Khmer
				0x1800, // 1800..18AF; Mongolian
				0x18B0, // 18B0..18FF; Unified Canadian Aboriginal Syllabics Extended
				0x1900, // 1900..194F; Limbu
				0x1950, // 1950..197F; Tai Le
				0x1980, // 1980..19DF; New Tai Lue
				0x19E0, // 19E0..19FF; Khmer Symbols
				0x1A00, // 1A00..1A1F; Buginese
				0x1A20, // 1A20..1AAF; Tai Tham
				0x1AB0, // unassigned
				0x1B00, // 1B00..1B7F; Balinese
				0x1B80, // 1B80..1BBF; Sundanese
				0x1BC0, // 1BC0..1BFF; Batak
				0x1C00, // 1C00..1C4F; Lepcha
				0x1C50, // 1C50..1C7F; Ol Chiki
				0x1C80, // unassigned
				0x1CD0, // 1CD0..1CFF; Vedic Extensions
				0x1D00, // 1D00..1D7F; Phonetic Extensions
				0x1D80, // 1D80..1DBF; Phonetic Extensions Supplement
				0x1DC0, // 1DC0..1DFF; Combining Diacritical Marks Supplement
				0x1E00, // 1E00..1EFF; Latin Extended Additional
				0x1F00, // 1F00..1FFF; Greek Extended
				0x2000, // 2000..206F; General Punctuation
				0x2070, // 2070..209F; Superscripts and Subscripts
				0x20A0, // 20A0..20CF; Currency Symbols
				0x20D0, // 20D0..20FF; Combining Diacritical Marks for Symbols
				0x2100, // 2100..214F; Letterlike Symbols
				0x2150, // 2150..218F; Number Forms
				0x2190, // 2190..21FF; Arrows
				0x2200, // 2200..22FF; Mathematical Operators
				0x2300, // 2300..23FF; Miscellaneous Technical
				0x2400, // 2400..243F; Control Pictures
				0x2440, // 2440..245F; Optical Character Recognition
				0x2460, // 2460..24FF; Enclosed Alphanumerics
				0x2500, // 2500..257F; Box Drawing
				0x2580, // 2580..259F; Block Elements
				0x25A0, // 25A0..25FF; Geometric Shapes
				0x2600, // 2600..26FF; Miscellaneous Symbols
				0x2700, // 2700..27BF; Dingbats
				0x27C0, // 27C0..27EF; Miscellaneous Mathematical Symbols-A
				0x27F0, // 27F0..27FF; Supplemental Arrows-A
				0x2800, // 2800..28FF; Braille Patterns
				0x2900, // 2900..297F; Supplemental Arrows-B
				0x2980, // 2980..29FF; Miscellaneous Mathematical Symbols-B
				0x2A00, // 2A00..2AFF; Supplemental Mathematical Operators
				0x2B00, // 2B00..2BFF; Miscellaneous Symbols and Arrows
				0x2C00, // 2C00..2C5F; Glagolitic
				0x2C60, // 2C60..2C7F; Latin Extended-C
				0x2C80, // 2C80..2CFF; Coptic
				0x2D00, // 2D00..2D2F; Georgian Supplement
				0x2D30, // 2D30..2D7F; Tifinagh
				0x2D80, // 2D80..2DDF; Ethiopic Extended
				0x2DE0, // 2DE0..2DFF; Cyrillic Extended-A
				0x2E00, // 2E00..2E7F; Supplemental Punctuation
				0x2E80, // 2E80..2EFF; CJK Radicals Supplement
				0x2F00, // 2F00..2FDF; Kangxi Radicals
				0x2FE0, // unassigned
				0x2FF0, // 2FF0..2FFF; Ideographic Description Characters
				0x3000, // 3000..303F; CJK Symbols and Punctuation
				0x3040, // 3040..309F; Hiragana
				0x30A0, // 30A0..30FF; Katakana
				0x3100, // 3100..312F; Bopomofo
				0x3130, // 3130..318F; Hangul Compatibility Jamo
				0x3190, // 3190..319F; Kanbun
				0x31A0, // 31A0..31BF; Bopomofo Extended
				0x31C0, // 31C0..31EF; CJK Strokes
				0x31F0, // 31F0..31FF; Katakana Phonetic Extensions
				0x3200, // 3200..32FF; Enclosed CJK Letters and Months
				0x3300, // 3300..33FF; CJK Compatibility
				0x3400, // 3400..4DBF; CJK Unified Ideographs Extension A
				0x4DC0, // 4DC0..4DFF; Yijing Hexagram Symbols
				0x4E00, // 4E00..9FFF; CJK Unified Ideographs
				0xA000, // A000..A48F; Yi Syllables
				0xA490, // A490..A4CF; Yi Radicals
				0xA4D0, // A4D0..A4FF; Lisu
				0xA500, // A500..A63F; Vai
				0xA640, // A640..A69F; Cyrillic Extended-B
				0xA6A0, // A6A0..A6FF; Bamum
				0xA700, // A700..A71F; Modifier Tone Letters
				0xA720, // A720..A7FF; Latin Extended-D
				0xA800, // A800..A82F; Syloti Nagri
				0xA830, // A830..A83F; Common Indic Number Forms
				0xA840, // A840..A87F; Phags-pa
				0xA880, // A880..A8DF; Saurashtra
				0xA8E0, // A8E0..A8FF; Devanagari Extended
				0xA900, // A900..A92F; Kayah Li
				0xA930, // A930..A95F; Rejang
				0xA960, // A960..A97F; Hangul Jamo Extended-A
				0xA980, // A980..A9DF; Javanese
				0xA9E0, // unassigned
				0xAA00, // AA00..AA5F; Cham
				0xAA60, // AA60..AA7F; Myanmar Extended-A
				0xAA80, // AA80..AADF; Tai Viet
				0xAAE0, // unassigned
				0xAB00, // AB00..AB2F; Ethiopic Extended-A
				0xAB30, // unassigned
				0xABC0, // ABC0..ABFF; Meetei Mayek
				0xAC00, // AC00..D7AF; Hangul Syllables
				0xD7B0, // D7B0..D7FF; Hangul Jamo Extended-B
				0xD800, // D800..DB7F; High Surrogates
				0xDB80, // DB80..DBFF; High Private Use Surrogates
				0xDC00, // DC00..DFFF; Low Surrogates
				0xE000, // E000..F8FF; Private Use Area
				0xF900, // F900..FAFF; CJK Compatibility Ideographs
				0xFB00, // FB00..FB4F; Alphabetic Presentation Forms
				0xFB50, // FB50..FDFF; Arabic Presentation Forms-A
				0xFE00, // FE00..FE0F; Variation Selectors
				0xFE10, // FE10..FE1F; Vertical Forms
				0xFE20, // FE20..FE2F; Combining Half Marks
				0xFE30, // FE30..FE4F; CJK Compatibility Forms
				0xFE50, // FE50..FE6F; Small Form Variants
				0xFE70, // FE70..FEFF; Arabic Presentation Forms-B
				0xFF00, // FF00..FFEF; Halfwidth and Fullwidth Forms
				0xFFF0, // FFF0..FFFF; Specials
				0x10000, // 10000..1007F; Linear B Syllabary
				0x10080, // 10080..100FF; Linear B Ideograms
				0x10100, // 10100..1013F; Aegean Numbers
				0x10140, // 10140..1018F; Ancient Greek Numbers
				0x10190, // 10190..101CF; Ancient Symbols
				0x101D0, // 101D0..101FF; Phaistos Disc
				0x10200, // unassigned
				0x10280, // 10280..1029F; Lycian
				0x102A0, // 102A0..102DF; Carian
				0x102E0, // unassigned
				0x10300, // 10300..1032F; Old Italic
				0x10330, // 10330..1034F; Gothic
				0x10350, // unassigned
				0x10380, // 10380..1039F; Ugaritic
				0x103A0, // 103A0..103DF; Old Persian
				0x103E0, // unassigned
				0x10400, // 10400..1044F; Deseret
				0x10450, // 10450..1047F; Shavian
				0x10480, // 10480..104AF; Osmanya
				0x104B0, // unassigned
				0x10800, // 10800..1083F; Cypriot Syllabary
				0x10840, // 10840..1085F; Imperial Aramaic
				0x10860, // unassigned
				0x10900, // 10900..1091F; Phoenician
				0x10920, // 10920..1093F; Lydian
				0x10940, // unassigned
				0x10A00, // 10A00..10A5F; Kharoshthi
				0x10A60, // 10A60..10A7F; Old South Arabian
				0x10A80, // unassigned
				0x10B00, // 10B00..10B3F; Avestan
				0x10B40, // 10B40..10B5F; Inscriptional Parthian
				0x10B60, // 10B60..10B7F; Inscriptional Pahlavi
				0x10B80, // unassigned
				0x10C00, // 10C00..10C4F; Old Turkic
				0x10C50, // unassigned
				0x10E60, // 10E60..10E7F; Rumi Numeral Symbols
				0x10E80, // unassigned
				0x11000, // 11000..1107F; Brahmi
				0x11080, // 11080..110CF; Kaithi
				0x110D0, // unassigned
				0x12000, // 12000..123FF; Cuneiform
				0x12400, // 12400..1247F; Cuneiform Numbers and Punctuation
				0x12480, // unassigned
				0x13000, // 13000..1342F; Egyptian Hieroglyphs
				0x13430, // unassigned
				0x16800, // 16800..16A3F; Bamum Supplement
				0x16A40, // unassigned
				0x1B000, // 1B000..1B0FF; Kana Supplement
				0x1B100, // unassigned
				0x1D000, // 1D000..1D0FF; Byzantine Musical Symbols
				0x1D100, // 1D100..1D1FF; Musical Symbols
				0x1D200, // 1D200..1D24F; Ancient Greek Musical Notation
				0x1D250, // unassigned
				0x1D300, // 1D300..1D35F; Tai Xuan Jing Symbols
				0x1D360, // 1D360..1D37F; Counting Rod Numerals
				0x1D380, // unassigned
				0x1D400, // 1D400..1D7FF; Mathematical Alphanumeric Symbols
				0x1D800, // unassigned
				0x1F000, // 1F000..1F02F; Mahjong Tiles
				0x1F030, // 1F030..1F09F; Domino Tiles
				0x1F0A0, // 1F0A0..1F0FF; Playing Cards
				0x1F100, // 1F100..1F1FF; Enclosed Alphanumeric Supplement
				0x1F200, // 1F200..1F2FF; Enclosed Ideographic Supplement
				0x1F300, // 1F300..1F5FF; Miscellaneous Symbols And Pictographs
				0x1F600, // 1F600..1F64F; Emoticons
				0x1F650, // unassigned
				0x1F680, // 1F680..1F6FF; Transport And Map Symbols
				0x1F700, // 1F700..1F77F; Alchemical Symbols
				0x1F780, // unassigned
				0x20000, // 20000..2A6DF; CJK Unified Ideographs Extension B
				0x2A6E0, // unassigned
				0x2A700, // 2A700..2B73F; CJK Unified Ideographs Extension C
				0x2B740, // 2B740..2B81F; CJK Unified Ideographs Extension D
				0x2B820, // unassigned
				0x2F800, // 2F800..2FA1F; CJK Compatibility Ideographs Supplement
				0x2FA20, // unassigned
				0xE0000, // E0000..E007F; Tags
				0xE0080, // unassigned
				0xE0100, // E0100..E01EF; Variation Selectors Supplement
				0xE01F0, // unassigned
				0xF0000, // F0000..FFFFF; Supplementary Private Use Area-A
				0x100000 // 100000..10FFFF; Supplementary Private Use Area-B
		};

		private static final UnicodeBlock[] blocks = { BASIC_LATIN, LATIN_1_SUPPLEMENT, LATIN_EXTENDED_A,
				LATIN_EXTENDED_B, IPA_EXTENSIONS, SPACING_MODIFIER_LETTERS, COMBINING_DIACRITICAL_MARKS,
				GREEK, CYRILLIC, CYRILLIC_SUPPLEMENTARY, ARMENIAN, HEBREW, ARABIC, SYRIAC, ARABIC_SUPPLEMENT,
				THAANA, NKO, SAMARITAN, MANDAIC, null, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, ORIYA, TAMIL,
				TELUGU, KANNADA, MALAYALAM, SINHALA, THAI, LAO, TIBETAN, MYANMAR, GEORGIAN, HANGUL_JAMO,
				ETHIOPIC, ETHIOPIC_SUPPLEMENT, CHEROKEE, UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS, OGHAM, RUNIC,
				TAGALOG, HANUNOO, BUHID, TAGBANWA, KHMER, MONGOLIAN,
				UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED, LIMBU, TAI_LE, NEW_TAI_LUE, KHMER_SYMBOLS,
				BUGINESE, TAI_THAM, null, BALINESE, SUNDANESE, BATAK, LEPCHA, OL_CHIKI, null,
				VEDIC_EXTENSIONS, PHONETIC_EXTENSIONS, PHONETIC_EXTENSIONS_SUPPLEMENT,
				COMBINING_DIACRITICAL_MARKS_SUPPLEMENT, LATIN_EXTENDED_ADDITIONAL, GREEK_EXTENDED,
				GENERAL_PUNCTUATION, SUPERSCRIPTS_AND_SUBSCRIPTS, CURRENCY_SYMBOLS,
				COMBINING_MARKS_FOR_SYMBOLS, LETTERLIKE_SYMBOLS, NUMBER_FORMS, ARROWS,
				MATHEMATICAL_OPERATORS, MISCELLANEOUS_TECHNICAL, CONTROL_PICTURES,
				OPTICAL_CHARACTER_RECOGNITION, ENCLOSED_ALPHANUMERICS, BOX_DRAWING, BLOCK_ELEMENTS,
				GEOMETRIC_SHAPES, MISCELLANEOUS_SYMBOLS, DINGBATS, MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A,
				SUPPLEMENTAL_ARROWS_A, BRAILLE_PATTERNS, SUPPLEMENTAL_ARROWS_B,
				MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B, SUPPLEMENTAL_MATHEMATICAL_OPERATORS,
				MISCELLANEOUS_SYMBOLS_AND_ARROWS, GLAGOLITIC, LATIN_EXTENDED_C, COPTIC, GEORGIAN_SUPPLEMENT,
				TIFINAGH, ETHIOPIC_EXTENDED, CYRILLIC_EXTENDED_A, SUPPLEMENTAL_PUNCTUATION,
				CJK_RADICALS_SUPPLEMENT, KANGXI_RADICALS, null, IDEOGRAPHIC_DESCRIPTION_CHARACTERS,
				CJK_SYMBOLS_AND_PUNCTUATION, HIRAGANA, KATAKANA, BOPOMOFO, HANGUL_COMPATIBILITY_JAMO, KANBUN,
				BOPOMOFO_EXTENDED, CJK_STROKES, KATAKANA_PHONETIC_EXTENSIONS,
				ENCLOSED_CJK_LETTERS_AND_MONTHS, CJK_COMPATIBILITY, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
				YIJING_HEXAGRAM_SYMBOLS, CJK_UNIFIED_IDEOGRAPHS, YI_SYLLABLES, YI_RADICALS, LISU, VAI,
				CYRILLIC_EXTENDED_B, BAMUM, MODIFIER_TONE_LETTERS, LATIN_EXTENDED_D, SYLOTI_NAGRI,
				COMMON_INDIC_NUMBER_FORMS, PHAGS_PA, SAURASHTRA, DEVANAGARI_EXTENDED, KAYAH_LI, REJANG,
				HANGUL_JAMO_EXTENDED_A, JAVANESE, null, CHAM, MYANMAR_EXTENDED_A, TAI_VIET, null,
				ETHIOPIC_EXTENDED_A, null, MEETEI_MAYEK, HANGUL_SYLLABLES, HANGUL_JAMO_EXTENDED_B,
				HIGH_SURROGATES, HIGH_PRIVATE_USE_SURROGATES, LOW_SURROGATES, PRIVATE_USE_AREA,
				CJK_COMPATIBILITY_IDEOGRAPHS, ALPHABETIC_PRESENTATION_FORMS, ARABIC_PRESENTATION_FORMS_A,
				VARIATION_SELECTORS, VERTICAL_FORMS, COMBINING_HALF_MARKS, CJK_COMPATIBILITY_FORMS,
				SMALL_FORM_VARIANTS, ARABIC_PRESENTATION_FORMS_B, HALFWIDTH_AND_FULLWIDTH_FORMS, SPECIALS,
				LINEAR_B_SYLLABARY, LINEAR_B_IDEOGRAMS, AEGEAN_NUMBERS, ANCIENT_GREEK_NUMBERS,
				ANCIENT_SYMBOLS, PHAISTOS_DISC, null, LYCIAN, CARIAN, null, OLD_ITALIC, GOTHIC, null,
				UGARITIC, OLD_PERSIAN, null, DESERET, SHAVIAN, OSMANYA, null, CYPRIOT_SYLLABARY,
				IMPERIAL_ARAMAIC, null, PHOENICIAN, LYDIAN, null, KHAROSHTHI, OLD_SOUTH_ARABIAN, null,
				AVESTAN, INSCRIPTIONAL_PARTHIAN, INSCRIPTIONAL_PAHLAVI, null, OLD_TURKIC, null,
				RUMI_NUMERAL_SYMBOLS, null, BRAHMI, KAITHI, null, CUNEIFORM,
				CUNEIFORM_NUMBERS_AND_PUNCTUATION, null, EGYPTIAN_HIEROGLYPHS, null, BAMUM_SUPPLEMENT, null,
				KANA_SUPPLEMENT, null, BYZANTINE_MUSICAL_SYMBOLS, MUSICAL_SYMBOLS,
				ANCIENT_GREEK_MUSICAL_NOTATION, null, TAI_XUAN_JING_SYMBOLS, COUNTING_ROD_NUMERALS, null,
				MATHEMATICAL_ALPHANUMERIC_SYMBOLS, null, MAHJONG_TILES, DOMINO_TILES, PLAYING_CARDS,
				ENCLOSED_ALPHANUMERIC_SUPPLEMENT, ENCLOSED_IDEOGRAPHIC_SUPPLEMENT,
				MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS, EMOTICONS, null, TRANSPORT_AND_MAP_SYMBOLS,
				ALCHEMICAL_SYMBOLS, null, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B, null,
				CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D, null,
				CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT, null, TAGS, null, VARIATION_SELECTORS_SUPPLEMENT,
				null, SUPPLEMENTARY_PRIVATE_USE_AREA_A, SUPPLEMENTARY_PRIVATE_USE_AREA_B };

		/**
		 * Returns the object representing the Unicode block containing the given character, or
		 * {@code null} if the character is not a member of a defined block.
		 *
		 * <p>
		 * <b>Note:</b> This method cannot handle <a href="Character.html#supplementary">
		 * supplementary characters</a>. To support all Unicode characters, including supplementary
		 * characters, use the {@link #of(int)} method.
		 *
		 * @param c The character in question
		 * @return The {@code UnicodeBlock} instance representing the Unicode block of which this
		 *         character is a member, or {@code null} if the character is not a member of any
		 *         Unicode block
		 */
		public static UnicodeBlock of(char c) {
			return of((int) c);
		}

		/**
		 * Returns the object representing the Unicode block containing the given character (Unicode
		 * code point), or {@code null} if the character is not a member of a defined block.
		 *
		 * @param codePoint the character (Unicode code point) in question.
		 * @return The {@code UnicodeBlock} instance representing the Unicode block of which this
		 *         character is a member, or {@code null} if the character is not a member of any
		 *         Unicode block
		 * @exception IllegalArgumentException if the specified {@code codePoint} is an invalid
		 *            Unicode code point.
		 * @see Character#isValidCodePoint(int)
		 * @since 1.5
		 */
		public static UnicodeBlock of(int codePoint) {
			if (!isValidCodePoint(codePoint)) {
				throw new IllegalArgumentException();
			}

			int top, bottom, current;
			bottom = 0;
			top = blockStarts.length;
			current = top / 2;

			// invariant: top > current >= bottom && codePoint >= unicodeBlockStarts[bottom]
			while (top - bottom > 1) {
				if (codePoint >= blockStarts[current]) {
					bottom = current;
				} else {
					top = current;
				}
				current = (top + bottom) / 2;
			}
			return blocks[current];
		}

		/**
		 * Returns the UnicodeBlock with the given name. Block names are determined by The Unicode
		 * Standard. The file Blocks-&lt;version&gt;.txt defines blocks for a particular version of
		 * the standard. The {@link Character} class specifies the version of the standard that it
		 * supports.
		 * <p>
		 * This method accepts block names in the following forms:
		 * <ol>
		 * <li>Canonical block names as defined by the Unicode Standard. For example, the standard
		 * defines a "Basic Latin" block. Therefore, this method accepts "Basic Latin" as a valid
		 * block name. The documentation of each UnicodeBlock provides the canonical name.
		 * <li>Canonical block names with all spaces removed. For example, "BasicLatin" is a valid
		 * block name for the "Basic Latin" block.
		 * <li>The text representation of each constant UnicodeBlock identifier. For example, this
		 * method will return the {@link #BASIC_LATIN} block if provided with the "BASIC_LATIN"
		 * name. This form replaces all spaces and hyphens in the canonical name with underscores.
		 * </ol>
		 * Finally, character case is ignored for all of the valid block name forms. For example,
		 * "BASIC_LATIN" and "basic_latin" are both valid block names. The en_US locale's case
		 * mapping rules are used to provide case-insensitive string comparisons for block name
		 * validation.
		 * <p>
		 * If the Unicode Standard changes block names, both the previous and current names will be
		 * accepted.
		 *
		 * @param blockName A {@code UnicodeBlock} name.
		 * @return The {@code UnicodeBlock} instance identified by {@code blockName}
		 * @throws IllegalArgumentException if {@code blockName} is an invalid name
		 * @throws NullPointerException if {@code blockName} is null
		 * @since 1.5
		 */
		public static final UnicodeBlock forName(String blockName) {
			UnicodeBlock block = map.get(blockName.toUpperCase(Locale.US));
			if (block == null) {
				throw new IllegalArgumentException();
			}
			return block;
		}
	}

	/**
	 * The value of the {@code Character}.
	 *
	 * @serial
	 */
	private final char value;

	/** use serialVersionUID from JDK 1.0.2 for interoperability */
	private static final long serialVersionUID = 3786198910865385080L;

	/**
	 * Constructs a newly allocated {@code Character} object that represents the specified
	 * {@code char} value.
	 *
	 * @param value the value to be represented by the {@code Character} object.
	 */
	public Character(char value) {
		this.value = value;
	}

	private static class CharacterCache {
		private CharacterCache() {
		}

		static final Character cache[] = new Character[127 + 1];

		static {
			for (int i = 0; i < cache.length; i++)
				cache[i] = new Character((char) i);
		}
	}

	/**
	 * Returns a <tt>Character</tt> instance representing the specified <tt>char</tt> value. If a
	 * new <tt>Character</tt> instance is not required, this method should generally be used in
	 * preference to the constructor {@link #Character(char)}, as this method is likely to yield
	 * significantly better space and time performance by caching frequently requested values.
	 *
	 * This method will always cache values in the range {@code '\u005Cu0000'} to
	 * {@code '\u005Cu007F'}, inclusive, and may cache other values outside of this range.
	 *
	 * @param c a char value.
	 * @return a <tt>Character</tt> instance representing <tt>c</tt>.
	 * @since 1.5
	 */
	public static Character valueOf(char c) {
		if (c <= 127) { // must cache
			return CharacterCache.cache[(int) c];
		}
		return new Character(c);
	}

	/**
	 * Returns the value of this {@code Character} object.
	 * 
	 * @return the primitive {@code char} value represented by this object.
	 */
	public char charValue() {
		return value;
	}

	/**
	 * Returns a hash code for this {@code Character}; equal to the result of invoking
	 * {@code charValue()}.
	 *
	 * @return a hash code value for this {@code Character}
	 */
	public int hashCode() {
		return (int) value;
	}

	/**
	 * Compares this object against the specified object. The result is {@code true} if and only if
	 * the argument is not {@code null} and is a {@code Character} object that represents the same
	 * {@code char} value as this object.
	 *
	 * @param obj the object to compare with.
	 * @return {@code true} if the objects are the same; {@code false} otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Character) {
			return value == ((Character) obj).charValue();
		}
		return false;
	}

	/**
	 * Returns a {@code String} object representing this {@code Character}'s value. The result is a
	 * string of length 1 whose sole component is the primitive {@code char} value represented by
	 * this {@code Character} object.
	 *
	 * @return a string representation of this object.
	 */
	public String toString() {
		char buf[] = { value };
		return String.valueOf(buf);
	}

	/**
	 * Returns a {@code String} object representing the specified {@code char}. The result is a
	 * string of length 1 consisting solely of the specified {@code char}.
	 *
	 * @param c the {@code char} to be converted
	 * @return the string representation of the specified {@code char}
	 * @since 1.4
	 */
	public static String toString(char c) {
		return String.valueOf(c);
	}

	/**
	 * Determines whether the specified code point is a valid <a
	 * href="http://www.unicode.org/glossary/#code_point"> Unicode code point value</a>.
	 *
	 * @param codePoint the Unicode code point to be tested
	 * @return {@code true} if the specified code point value is between {@link #MIN_CODE_POINT} and
	 *         {@link #MAX_CODE_POINT} inclusive; {@code false} otherwise.
	 * @since 1.5
	 */
	public static boolean isValidCodePoint(int codePoint) {
		// Optimized form of:
		// codePoint >= MIN_CODE_POINT && codePoint <= MAX_CODE_POINT
		int plane = codePoint >>> 16;
		return plane < ((MAX_CODE_POINT + 1) >>> 16);
	}

	/**
	 * Determines whether the specified character (Unicode code point) is in the <a
	 * href="#BMP">Basic Multilingual Plane (BMP)</a>. Such code points can be represented using a
	 * single {@code char}.
	 *
	 * @param codePoint the character (Unicode code point) to be tested
	 * @return {@code true} if the specified code point is between {@link #MIN_VALUE} and
	 *         {@link #MAX_VALUE} inclusive; {@code false} otherwise.
	 * @since 1.7
	 */
	public static boolean isBmpCodePoint(int codePoint) {
		return codePoint >>> 16 == 0;
		// Optimized form of:
		// codePoint >= MIN_VALUE && codePoint <= MAX_VALUE
		// We consistently use logical shift (>>>) to facilitate
		// additional runtime optimizations.
	}

	/**
	 * Determines whether the specified character (Unicode code point) is in the <a
	 * href="#supplementary">supplementary character</a> range.
	 *
	 * @param codePoint the character (Unicode code point) to be tested
	 * @return {@code true} if the specified code point is between
	 *         {@link #MIN_SUPPLEMENTARY_CODE_POINT} and {@link #MAX_CODE_POINT} inclusive;
	 *         {@code false} otherwise.
	 * @since 1.5
	 */
	public static boolean isSupplementaryCodePoint(int codePoint) {
		return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT && codePoint < MAX_CODE_POINT + 1;
	}

	/**
	 * Determines if the given {@code char} value is a <a
	 * href="http://www.unicode.org/glossary/#high_surrogate_code_unit"> Unicode high-surrogate code
	 * unit</a> (also known as <i>leading-surrogate code unit</i>).
	 *
	 * <p>
	 * Such values do not represent characters by themselves, but are used in the representation of
	 * <a href="#supplementary">supplementary characters</a> in the UTF-16 encoding.
	 *
	 * @param ch the {@code char} value to be tested.
	 * @return {@code true} if the {@code char} value is between {@link #MIN_HIGH_SURROGATE} and
	 *         {@link #MAX_HIGH_SURROGATE} inclusive; {@code false} otherwise.
	 * @see Character#isLowSurrogate(char)
	 * @see Character.UnicodeBlock#of(int)
	 * @since 1.5
	 */
	public static boolean isHighSurrogate(char ch) {
		// Help VM constant-fold; MAX_HIGH_SURROGATE + 1 == MIN_LOW_SURROGATE
		return ch >= MIN_HIGH_SURROGATE && ch < (MAX_HIGH_SURROGATE + 1);
	}

	/**
	 * Determines if the given {@code char} value is a <a
	 * href="http://www.unicode.org/glossary/#low_surrogate_code_unit"> Unicode low-surrogate code
	 * unit</a> (also known as <i>trailing-surrogate code unit</i>).
	 *
	 * <p>
	 * Such values do not represent characters by themselves, but are used in the representation of
	 * <a href="#supplementary">supplementary characters</a> in the UTF-16 encoding.
	 *
	 * @param ch the {@code char} value to be tested.
	 * @return {@code true} if the {@code char} value is between {@link #MIN_LOW_SURROGATE} and
	 *         {@link #MAX_LOW_SURROGATE} inclusive; {@code false} otherwise.
	 * @see Character#isHighSurrogate(char)
	 * @since 1.5
	 */
	public static boolean isLowSurrogate(char ch) {
		return ch >= MIN_LOW_SURROGATE && ch < (MAX_LOW_SURROGATE + 1);
	}

	/**
	 * Determines if the given {@code char} value is a Unicode <i>surrogate code unit</i>.
	 *
	 * <p>
	 * Such values do not represent characters by themselves, but are used in the representation of
	 * <a href="#supplementary">supplementary characters</a> in the UTF-16 encoding.
	 *
	 * <p>
	 * A char value is a surrogate code unit if and only if it is either a
	 * {@linkplain #isLowSurrogate(char) low-surrogate code unit} or a
	 * {@linkplain #isHighSurrogate(char) high-surrogate code unit}.
	 *
	 * @param ch the {@code char} value to be tested.
	 * @return {@code true} if the {@code char} value is between {@link #MIN_SURROGATE} and
	 *         {@link #MAX_SURROGATE} inclusive; {@code false} otherwise.
	 * @since 1.7
	 */
	public static boolean isSurrogate(char ch) {
		return ch >= MIN_SURROGATE && ch < (MAX_SURROGATE + 1);
	}

	/**
	 * Determines whether the specified pair of {@code char} values is a valid <a
	 * href="http://www.unicode.org/glossary/#surrogate_pair"> Unicode surrogate pair</a>.
	 * 
	 * <p>
	 * This method is equivalent to the expression: <blockquote>
	 * 
	 * <pre>
	 * isHighSurrogate(high) &amp;&amp; isLowSurrogate(low)
	 * </pre>
	 * 
	 * </blockquote>
	 *
	 * @param high the high-surrogate code value to be tested
	 * @param low the low-surrogate code value to be tested
	 * @return {@code true} if the specified high and low-surrogate code values represent a valid
	 *         surrogate pair; {@code false} otherwise.
	 * @since 1.5
	 */
	public static boolean isSurrogatePair(char high, char low) {
		return isHighSurrogate(high) && isLowSurrogate(low);
	}

	/**
	 * Determines the number of {@code char} values needed to represent the specified character
	 * (Unicode code point). If the specified character is equal to or greater than 0x10000, then
	 * the method returns 2. Otherwise, the method returns 1.
	 *
	 * <p>
	 * This method doesn't validate the specified character to be a valid Unicode code point. The
	 * caller must validate the character value using {@link #isValidCodePoint(int)
	 * isValidCodePoint} if necessary.
	 *
	 * @param codePoint the character (Unicode code point) to be tested.
	 * @return 2 if the character is a valid supplementary character; 1 otherwise.
	 * @see Character#isSupplementaryCodePoint(int)
	 * @since 1.5
	 */
	public static int charCount(int codePoint) {
		return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT ? 2 : 1;
	}

	/**
	 * Converts the specified surrogate pair to its supplementary code point value. This method does
	 * not validate the specified surrogate pair. The caller must validate it using
	 * {@link #isSurrogatePair(char, char) isSurrogatePair} if necessary.
	 *
	 * @param high the high-surrogate code unit
	 * @param low the low-surrogate code unit
	 * @return the supplementary code point composed from the specified surrogate pair.
	 * @since 1.5
	 */
	public static int toCodePoint(char high, char low) {
		// Optimized form of:
		// return ((high - MIN_HIGH_SURROGATE) << 10)
		// + (low - MIN_LOW_SURROGATE)
		// + MIN_SUPPLEMENTARY_CODE_POINT;
		return ((high << 10) + low)
				+ (MIN_SUPPLEMENTARY_CODE_POINT - (MIN_HIGH_SURROGATE << 10) - MIN_LOW_SURROGATE);
	}

	/**
	 * Returns the code point at the given index of the {@code CharSequence}. If the {@code char}
	 * value at the given index in the {@code CharSequence} is in the high-surrogate range, the
	 * following index is less than the length of the {@code CharSequence}, and the {@code char}
	 * value at the following index is in the low-surrogate range, then the supplementary code point
	 * corresponding to this surrogate pair is returned. Otherwise, the {@code char} value at the
	 * given index is returned.
	 *
	 * @param seq a sequence of {@code char} values (Unicode code units)
	 * @param index the index to the {@code char} values (Unicode code units) in {@code seq} to be
	 *        converted
	 * @return the Unicode code point at the given index
	 * @exception NullPointerException if {@code seq} is null.
	 * @exception IndexOutOfBoundsException if the value {@code index} is negative or not less than
	 *            {@link CharSequence#length() seq.length()}.
	 * @since 1.5
	 */
	public static int codePointAt(CharSequence seq, int index) {
		char c1 = seq.charAt(index++);
		if (isHighSurrogate(c1)) {
			if (index < seq.length()) {
				char c2 = seq.charAt(index);
				if (isLowSurrogate(c2)) {
					return toCodePoint(c1, c2);
				}
			}
		}
		return c1;
	}

	/**
	 * Returns the code point at the given index of the {@code char} array. If the {@code char}
	 * value at the given index in the {@code char} array is in the high-surrogate range, the
	 * following index is less than the length of the {@code char} array, and the {@code char} value
	 * at the following index is in the low-surrogate range, then the supplementary code point
	 * corresponding to this surrogate pair is returned. Otherwise, the {@code char} value at the
	 * given index is returned.
	 *
	 * @param a the {@code char} array
	 * @param index the index to the {@code char} values (Unicode code units) in the {@code char}
	 *        array to be converted
	 * @return the Unicode code point at the given index
	 * @exception NullPointerException if {@code a} is null.
	 * @exception IndexOutOfBoundsException if the value {@code index} is negative or not less than
	 *            the length of the {@code char} array.
	 * @since 1.5
	 */
	public static int codePointAt(char[] a, int index) {
		return codePointAtImpl(a, index, a.length);
	}

	/**
	 * Returns the code point at the given index of the {@code char} array, where only array
	 * elements with {@code index} less than {@code limit} can be used. If the {@code char} value at
	 * the given index in the {@code char} array is in the high-surrogate range, the following index
	 * is less than the {@code limit}, and the {@code char} value at the following index is in the
	 * low-surrogate range, then the supplementary code point corresponding to this surrogate pair
	 * is returned. Otherwise, the {@code char} value at the given index is returned.
	 *
	 * @param a the {@code char} array
	 * @param index the index to the {@code char} values (Unicode code units) in the {@code char}
	 *        array to be converted
	 * @param limit the index after the last array element that can be used in the {@code char}
	 *        array
	 * @return the Unicode code point at the given index
	 * @exception NullPointerException if {@code a} is null.
	 * @exception IndexOutOfBoundsException if the {@code index} argument is negative or not less
	 *            than the {@code limit} argument, or if the {@code limit} argument is negative or
	 *            greater than the length of the {@code char} array.
	 * @since 1.5
	 */
	public static int codePointAt(char[] a, int index, int limit) {
		if (index >= limit || limit < 0 || limit > a.length) {
			throw new IndexOutOfBoundsException();
		}
		return codePointAtImpl(a, index, limit);
	}

	// throws ArrayIndexOutofBoundsException if index out of bounds
	static int codePointAtImpl(char[] a, int index, int limit) {
		char c1 = a[index++];
		if (isHighSurrogate(c1)) {
			if (index < limit) {
				char c2 = a[index];
				if (isLowSurrogate(c2)) {
					return toCodePoint(c1, c2);
				}
			}
		}
		return c1;
	}

	/**
	 * Returns the code point preceding the given index of the {@code CharSequence}. If the
	 * {@code char} value at {@code (index - 1)} in the {@code CharSequence} is in the low-surrogate
	 * range, {@code (index - 2)} is not negative, and the {@code char} value at {@code (index - 2)}
	 * in the {@code CharSequence} is in the high-surrogate range, then the supplementary code point
	 * corresponding to this surrogate pair is returned. Otherwise, the {@code char} value at
	 * {@code (index - 1)} is returned.
	 *
	 * @param seq the {@code CharSequence} instance
	 * @param index the index following the code point that should be returned
	 * @return the Unicode code point value before the given index.
	 * @exception NullPointerException if {@code seq} is null.
	 * @exception IndexOutOfBoundsException if the {@code index} argument is less than 1 or greater
	 *            than {@link CharSequence#length() seq.length()}.
	 * @since 1.5
	 */
	public static int codePointBefore(CharSequence seq, int index) {
		char c2 = seq.charAt(--index);
		if (isLowSurrogate(c2)) {
			if (index > 0) {
				char c1 = seq.charAt(--index);
				if (isHighSurrogate(c1)) {
					return toCodePoint(c1, c2);
				}
			}
		}
		return c2;
	}

	/**
	 * Returns the code point preceding the given index of the {@code char} array. If the
	 * {@code char} value at {@code (index - 1)} in the {@code char} array is in the low-surrogate
	 * range, {@code (index - 2)} is not negative, and the {@code char} value at {@code (index - 2)}
	 * in the {@code char} array is in the high-surrogate range, then the supplementary code point
	 * corresponding to this surrogate pair is returned. Otherwise, the {@code char} value at
	 * {@code (index - 1)} is returned.
	 *
	 * @param a the {@code char} array
	 * @param index the index following the code point that should be returned
	 * @return the Unicode code point value before the given index.
	 * @exception NullPointerException if {@code a} is null.
	 * @exception IndexOutOfBoundsException if the {@code index} argument is less than 1 or greater
	 *            than the length of the {@code char} array
	 * @since 1.5
	 */
	public static int codePointBefore(char[] a, int index) {
		return codePointBeforeImpl(a, index, 0);
	}

	/**
	 * Returns the code point preceding the given index of the {@code char} array, where only array
	 * elements with {@code index} greater than or equal to {@code start} can be used. If the
	 * {@code char} value at {@code (index - 1)} in the {@code char} array is in the low-surrogate
	 * range, {@code (index - 2)} is not less than {@code start}, and the {@code char} value at
	 * {@code (index - 2)} in the {@code char} array is in the high-surrogate range, then the
	 * supplementary code point corresponding to this surrogate pair is returned. Otherwise, the
	 * {@code char} value at {@code (index - 1)} is returned.
	 *
	 * @param a the {@code char} array
	 * @param index the index following the code point that should be returned
	 * @param start the index of the first array element in the {@code char} array
	 * @return the Unicode code point value before the given index.
	 * @exception NullPointerException if {@code a} is null.
	 * @exception IndexOutOfBoundsException if the {@code index} argument is not greater than the
	 *            {@code start} argument or is greater than the length of the {@code char} array, or
	 *            if the {@code start} argument is negative or not less than the length of the
	 *            {@code char} array.
	 * @since 1.5
	 */
	public static int codePointBefore(char[] a, int index, int start) {
		if (index <= start || start < 0 || start >= a.length) {
			throw new IndexOutOfBoundsException();
		}
		return codePointBeforeImpl(a, index, start);
	}

	// throws ArrayIndexOutofBoundsException if index-1 out of bounds
	static int codePointBeforeImpl(char[] a, int index, int start) {
		char c2 = a[--index];
		if (isLowSurrogate(c2)) {
			if (index > start) {
				char c1 = a[--index];
				if (isHighSurrogate(c1)) {
					return toCodePoint(c1, c2);
				}
			}
		}
		return c2;
	}

	/**
	 * Returns the leading surrogate (a <a
	 * href="http://www.unicode.org/glossary/#high_surrogate_code_unit"> high surrogate code
	 * unit</a>) of the <a href="http://www.unicode.org/glossary/#surrogate_pair"> surrogate
	 * pair</a> representing the specified supplementary character (Unicode code point) in the
	 * UTF-16 encoding. If the specified character is not a <a
	 * href="Character.html#supplementary">supplementary character</a>, an unspecified {@code char}
	 * is returned.
	 *
	 * <p>
	 * If {@link #isSupplementaryCodePoint isSupplementaryCodePoint(x)} is {@code true}, then
	 * {@link #isHighSurrogate isHighSurrogate}{@code (highSurrogate(x))} and {@link #toCodePoint
	 * toCodePoint}{@code (highSurrogate(x), }{@link #lowSurrogate lowSurrogate}{@code (x)) == x}
	 * are also always {@code true}.
	 *
	 * @param codePoint a supplementary character (Unicode code point)
	 * @return the leading surrogate code unit used to represent the character in the UTF-16
	 *         encoding
	 * @since 1.7
	 */
	public static char highSurrogate(int codePoint) {
		return (char) ((codePoint >>> 10) + (MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
	}

	/**
	 * Returns the trailing surrogate (a <a
	 * href="http://www.unicode.org/glossary/#low_surrogate_code_unit"> low surrogate code unit</a>)
	 * of the <a href="http://www.unicode.org/glossary/#surrogate_pair"> surrogate pair</a>
	 * representing the specified supplementary character (Unicode code point) in the UTF-16
	 * encoding. If the specified character is not a <a
	 * href="Character.html#supplementary">supplementary character</a>, an unspecified {@code char}
	 * is returned.
	 *
	 * <p>
	 * If {@link #isSupplementaryCodePoint isSupplementaryCodePoint(x)} is {@code true}, then
	 * {@link #isLowSurrogate isLowSurrogate}{@code (lowSurrogate(x))} and {@link #toCodePoint
	 * toCodePoint}{@code (}{@link #highSurrogate highSurrogate}{@code (x), lowSurrogate(x)) == x}
	 * are also always {@code true}.
	 *
	 * @param codePoint a supplementary character (Unicode code point)
	 * @return the trailing surrogate code unit used to represent the character in the UTF-16
	 *         encoding
	 * @since 1.7
	 */
	public static char lowSurrogate(int codePoint) {
		return (char) ((codePoint & 0x3ff) + MIN_LOW_SURROGATE);
	}

	/**
	 * Converts the specified character (Unicode code point) to its UTF-16 representation. If the
	 * specified code point is a BMP (Basic Multilingual Plane or Plane 0) value, the same value is
	 * stored in {@code dst[dstIndex]}, and 1 is returned. If the specified code point is a
	 * supplementary character, its surrogate values are stored in {@code dst[dstIndex]}
	 * (high-surrogate) and {@code dst[dstIndex+1]} (low-surrogate), and 2 is returned.
	 *
	 * @param codePoint the character (Unicode code point) to be converted.
	 * @param dst an array of {@code char} in which the {@code codePoint}'s UTF-16 value is stored.
	 * @param dstIndex the start index into the {@code dst} array where the converted value is
	 *        stored.
	 * @return 1 if the code point is a BMP code point, 2 if the code point is a supplementary code
	 *         point.
	 * @exception IllegalArgumentException if the specified {@code codePoint} is not a valid Unicode
	 *            code point.
	 * @exception NullPointerException if the specified {@code dst} is null.
	 * @exception IndexOutOfBoundsException if {@code dstIndex} is negative or not less than
	 *            {@code dst.length}, or if {@code dst} at {@code dstIndex} doesn't have enough
	 *            array element(s) to store the resulting {@code char} value(s). (If
	 *            {@code dstIndex} is equal to {@code dst.length-1} and the specified
	 *            {@code codePoint} is a supplementary character, the high-surrogate value is not
	 *            stored in {@code dst[dstIndex]}.)
	 * @since 1.5
	 */
	public static int toChars(int codePoint, char[] dst, int dstIndex) {
		if (isBmpCodePoint(codePoint)) {
			dst[dstIndex] = (char) codePoint;
			return 1;
		} else if (isValidCodePoint(codePoint)) {
			toSurrogates(codePoint, dst, dstIndex);
			return 2;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Converts the specified character (Unicode code point) to its UTF-16 representation stored in
	 * a {@code char} array. If the specified code point is a BMP (Basic Multilingual Plane or Plane
	 * 0) value, the resulting {@code char} array has the same value as {@code codePoint}. If the
	 * specified code point is a supplementary code point, the resulting {@code char} array has the
	 * corresponding surrogate pair.
	 *
	 * @param codePoint a Unicode code point
	 * @return a {@code char} array having {@code codePoint}'s UTF-16 representation.
	 * @exception IllegalArgumentException if the specified {@code codePoint} is not a valid Unicode
	 *            code point.
	 * @since 1.5
	 */
	public static char[] toChars(int codePoint) {
		if (isBmpCodePoint(codePoint)) {
			return new char[] { (char) codePoint };
		} else if (isValidCodePoint(codePoint)) {
			char[] result = new char[2];
			toSurrogates(codePoint, result, 0);
			return result;
		} else {
			throw new IllegalArgumentException();
		}
	}

	static void toSurrogates(int codePoint, char[] dst, int index) {
		// We write elements "backwards" to guarantee all-or-nothing
		dst[index + 1] = lowSurrogate(codePoint);
		dst[index] = highSurrogate(codePoint);
	}

	/**
	 * Returns the number of Unicode code points in the text range of the specified char sequence.
	 * The text range begins at the specified {@code beginIndex} and extends to the {@code char} at
	 * index {@code endIndex - 1}. Thus the length (in {@code char}s) of the text range is
	 * {@code endIndex-beginIndex}. Unpaired surrogates within the text range count as one code
	 * point each.
	 *
	 * @param seq the char sequence
	 * @param beginIndex the index to the first {@code char} of the text range.
	 * @param endIndex the index after the last {@code char} of the text range.
	 * @return the number of Unicode code points in the specified text range
	 * @exception NullPointerException if {@code seq} is null.
	 * @exception IndexOutOfBoundsException if the {@code beginIndex} is negative, or
	 *            {@code endIndex} is larger than the length of the given sequence, or
	 *            {@code beginIndex} is larger than {@code endIndex}.
	 * @since 1.5
	 */
	public static int codePointCount(CharSequence seq, int beginIndex, int endIndex) {
		int length = seq.length();
		if (beginIndex < 0 || endIndex > length || beginIndex > endIndex) {
			throw new IndexOutOfBoundsException();
		}
		int n = endIndex - beginIndex;
		for (int i = beginIndex; i < endIndex;) {
			if (isHighSurrogate(seq.charAt(i++)) && i < endIndex && isLowSurrogate(seq.charAt(i))) {
				n--;
				i++;
			}
		}
		return n;
	}

	/**
	 * Returns the number of Unicode code points in a subarray of the {@code char} array argument.
	 * The {@code offset} argument is the index of the first {@code char} of the subarray and the
	 * {@code count} argument specifies the length of the subarray in {@code char}s. Unpaired
	 * surrogates within the subarray count as one code point each.
	 *
	 * @param a the {@code char} array
	 * @param offset the index of the first {@code char} in the given {@code char} array
	 * @param count the length of the subarray in {@code char}s
	 * @return the number of Unicode code points in the specified subarray
	 * @exception NullPointerException if {@code a} is null.
	 * @exception IndexOutOfBoundsException if {@code offset} or {@code count} is negative, or if
	 *            {@code offset +
	 * count} is larger than the length of the given array.
	 * @since 1.5
	 */
	public static int codePointCount(char[] a, int offset, int count) {
		if (count > a.length - offset || offset < 0 || count < 0) {
			throw new IndexOutOfBoundsException();
		}
		return codePointCountImpl(a, offset, count);
	}

	static int codePointCountImpl(char[] a, int offset, int count) {
		int endIndex = offset + count;
		int n = count;
		for (int i = offset; i < endIndex;) {
			if (isHighSurrogate(a[i++]) && i < endIndex && isLowSurrogate(a[i])) {
				n--;
				i++;
			}
		}
		return n;
	}

	/**
	 * Returns the index within the given char sequence that is offset from the given {@code index}
	 * by {@code codePointOffset} code points. Unpaired surrogates within the text range given by
	 * {@code index} and {@code codePointOffset} count as one code point each.
	 *
	 * @param seq the char sequence
	 * @param index the index to be offset
	 * @param codePointOffset the offset in code points
	 * @return the index within the char sequence
	 * @exception NullPointerException if {@code seq} is null.
	 * @exception IndexOutOfBoundsException if {@code index} is negative or larger then the length
	 *            of the char sequence, or if {@code codePointOffset} is positive and the
	 *            subsequence starting with {@code index} has fewer than {@code codePointOffset}
	 *            code points, or if {@code codePointOffset} is negative and the subsequence before
	 *            {@code index} has fewer than the absolute value of {@code codePointOffset} code
	 *            points.
	 * @since 1.5
	 */
	public static int offsetByCodePoints(CharSequence seq, int index, int codePointOffset) {
		int length = seq.length();
		if (index < 0 || index > length) {
			throw new IndexOutOfBoundsException();
		}

		int x = index;
		if (codePointOffset >= 0) {
			int i;
			for (i = 0; x < length && i < codePointOffset; i++) {
				if (isHighSurrogate(seq.charAt(x++)) && x < length && isLowSurrogate(seq.charAt(x))) {
					x++;
				}
			}
			if (i < codePointOffset) {
				throw new IndexOutOfBoundsException();
			}
		} else {
			int i;
			for (i = codePointOffset; x > 0 && i < 0; i++) {
				if (isLowSurrogate(seq.charAt(--x)) && x > 0 && isHighSurrogate(seq.charAt(x - 1))) {
					x--;
				}
			}
			if (i < 0) {
				throw new IndexOutOfBoundsException();
			}
		}
		return x;
	}

	/**
	 * Returns the index within the given {@code char} subarray that is offset from the given
	 * {@code index} by {@code codePointOffset} code points. The {@code start} and {@code count}
	 * arguments specify a subarray of the {@code char} array. Unpaired surrogates within the text
	 * range given by {@code index} and {@code codePointOffset} count as one code point each.
	 *
	 * @param a the {@code char} array
	 * @param start the index of the first {@code char} of the subarray
	 * @param count the length of the subarray in {@code char}s
	 * @param index the index to be offset
	 * @param codePointOffset the offset in code points
	 * @return the index within the subarray
	 * @exception NullPointerException if {@code a} is null.
	 * @exception IndexOutOfBoundsException if {@code start} or {@code count} is negative, or if
	 *            {@code start + count} is larger than the length of the given array, or if
	 *            {@code index} is less than {@code start} or larger then {@code start + count}, or
	 *            if {@code codePointOffset} is positive and the text range starting with
	 *            {@code index} and ending with {@code start + count - 1} has fewer than
	 *            {@code codePointOffset} code points, or if {@code codePointOffset} is negative and
	 *            the text range starting with {@code start} and ending with {@code index - 1} has
	 *            fewer than the absolute value of {@code codePointOffset} code points.
	 * @since 1.5
	 */
	public static int offsetByCodePoints(char[] a, int start, int count, int index, int codePointOffset) {
		if (count > a.length - start || start < 0 || count < 0 || index < start || index > start + count) {
			throw new IndexOutOfBoundsException();
		}
		return offsetByCodePointsImpl(a, start, count, index, codePointOffset);
	}

	static int offsetByCodePointsImpl(char[] a, int start, int count, int index, int codePointOffset) {
		int x = index;
		if (codePointOffset >= 0) {
			int limit = start + count;
			int i;
			for (i = 0; x < limit && i < codePointOffset; i++) {
				if (isHighSurrogate(a[x++]) && x < limit && isLowSurrogate(a[x])) {
					x++;
				}
			}
			if (i < codePointOffset) {
				throw new IndexOutOfBoundsException();
			}
		} else {
			int i;
			for (i = codePointOffset; x > start && i < 0; i++) {
				if (isLowSurrogate(a[--x]) && x > start && isHighSurrogate(a[x - 1])) {
					x--;
				}
			}
			if (i < 0) {
				throw new IndexOutOfBoundsException();
			}
		}
		return x;
	}

}
