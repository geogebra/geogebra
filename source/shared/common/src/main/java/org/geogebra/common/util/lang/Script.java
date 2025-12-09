/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.util.lang;

/**
 * 
 * https://en.wikipedia.org/wiki/ISO_15924
 * https://stackoverflow.com/questions/16447807/get-an-iso-15924-script-code-for
 * -a-given-ietf-language-tag-or-iso-639-iso-3166
 *
 */
@SuppressWarnings("javadoc")
public enum Script {

	ARABIC("Arab", null, Direction.RIGHT_TO_LEFT),

	ARMENIAN("Armn", "\u0570"),

	BENGALI("Beng"), // NO-TYPO

	CYRILLIC("Cyrl", "\u0439"),

	DEVANAGARI("Deva", "\u0947"),

	ETHIOPIAN("Ethi", "\u134b\u12ed\u120d"),

	GEORGIAN("Geor", "\u10d8"),

	GREEK("Grek"),

	HEBREW("Hebr", "\u05d9\u05b4", Direction.RIGHT_TO_LEFT),

	/** Simplified Chinese */
	HANS("Hans", "\u984F"),

	/** Traditional Chinese */
	HANT("Hant", "\u984F"),

	JAPANESE("Jpan", "\uff9d"),

	KANNADA("Knda", "\u0CAE"),

	KHMER("Khmr"),

	KOREAN("Kore", "\u1103"),

	LATIN("Latn"),

	MALAYALAM("Mlym", "\u0D2E"),

	MONG("Mong"),

	MYANMAR("Mymr"),

	SINHALA("Sinh", "\u0d9a"),

	TAMIL("Taml", "\u0be7"),

	TELUGU("Telu"),

	THAI("Thai", "\u0E20\u0E32");

	final public String iso15924;
	final public Direction direction;
	final public String testString;

	Script(String iso15924) {
		this(iso15924, null, Direction.LEFT_TO_RIGHT);
	}

	Script(String iso15924, String testString) {
		this(iso15924, testString, Direction.LEFT_TO_RIGHT);
	}

	Script(String iso15924, String testString, Direction dir) {
		this.iso15924 = iso15924;
		this.testString = testString;
		this.direction = dir;
	}
}
