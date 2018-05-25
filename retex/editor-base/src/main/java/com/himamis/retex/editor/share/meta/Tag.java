package com.himamis.retex.editor.share.meta;

public enum Tag {
	CEIL("ceil"),

	FLOOR("floor"),

	SQUARE,

	REGULAR,

	CURLY,

	APOSTROPHES,

	LINE,

	MATRIX,

	CHAR,

	SUPERSCRIPT("^"),

	SUBSCRIPT("_"),

	FRAC("frac"),

	SQRT("sqrt"),

	NROOT("nroot"),

	PROD("prod"),

	INT("int"),

	LIM("lim"),

	APPLY,

	LOG2("log2"),

	LOG10("log10"),

	ABS("abs"),

	SUM("sum"),

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
	 * @param casName
	 *            name
	 * @return tag with given name
	 */
	public static Tag lookup(String casName) {
		if (casName == null || casName.length() == 0) {
			return null;
		}
		for (Tag tag : Tag.values()) {
			if (casName.equals(tag.key)) {
				return tag;
			}
		}
		return null;
	}

	public String getFunction() {
		return key;
	}

}
