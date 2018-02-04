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

	ARABIC("Arab", Direction.RIGHT_TO_LEFT),

	ARMENIAN("Armn"),

	BENGALI("Beng"),

	CYRILLIC("Cyrl"),

	DEVANGARI("Deva"),

	ETHIOPIAN("Ethi"),

	GEORGIAN("Geor"),

	GREEK("Grek"),

	HEBREW("Hebr", Direction.RIGHT_TO_LEFT),

	/** Simplified Chinese */
	HANS("Hans"),

	/** Traditional Chinese */
	HANT("Hant"),

	JAPANESE("Jpan"),

	KANNADA("Knda"),

	KHMER("Khmr"),

	KOREAN("Kore"),

	LATIN("Latn"),

	MALAYALAM("Mlym"),

	MONG("Mong"),

	MYANMAR("Mymr"),

	SINHALA("Sinh"),

	TAMIL("Taml"),

	TELUGU("Telu"),

	THAI("Thai");

	final public String iso15924;
	final public Direction direction;

	Script(String iso15924) {

		this(iso15924, Direction.LEFT_TO_RIGHT);
	}

	Script(String iso15924, Direction dir) {
		this.iso15924 = iso15924;
		this.direction = dir;
	}

}
