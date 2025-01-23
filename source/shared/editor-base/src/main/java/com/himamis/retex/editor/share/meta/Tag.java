package com.himamis.retex.editor.share.meta;

public enum Tag {
	CEIL("ceil"),

	FLOOR("floor"),

	SQUARE,

	REGULAR,

	CURLY,

	APOSTROPHES,

	MATRIX,

	CHAR,

	SUPERSCRIPT("^"),

	SUBSCRIPT("_"),

	FRAC("frac"),

	SQRT("sqrt"),

	CBRT("cbrt"),

	NROOT("nroot"),

	LOG("log"),

	DEF_INT("$defint"),

	LIM_EQ("$limeq"),

	PROD_EQ("$prodeq"),

	SUM_EQ("$sumeq"),

	VEC("$vec"),

	ATOMIC_PRE("$atomicpre"),

	ATOMIC_POST("$atomicpost"),

	POINT("$point"),

	POINT_AT("$pointAt"),

	MIXED_NUMBER("mixedNumber"),

	RECURRING_DECIMAL("recurringDecimal"),

	APPLY,

	ABS("abs"),

	APPLY_SQUARE;

	private final String key;

	Tag(String c) {
		key = c;
	}

	Tag() {
		key = "";
	}

	public char getKey() {
		return key.length() == 1 ? key.charAt(0) : 0;
	}

	/**
	 * @param name
	 *            name
	 * @return tag with given name
	 */
	public static Tag lookup(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}
		for (Tag tag : Tag.values()) {
			if (name.equals(tag.key)) {
				return tag;
			}
		}
		return null;
	}

	public String getFunction() {
		return key;
	}

	/**
	 * @return whether template has highlighted boxes
	 */
	public boolean isRenderingOwnPlaceholders() {
		switch (this) {
		case POINT:
		case POINT_AT:
			return true;
		default: return false;
		}
	}
}
