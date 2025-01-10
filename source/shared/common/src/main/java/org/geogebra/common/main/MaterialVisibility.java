package org.geogebra.common.main;

/** Material visibility */
public enum MaterialVisibility {
	/** private */
	Private(0, "P"),
	/** shared with link */
	Shared(1, "S"),
	/** public */
	Public(2, "O");

	private int index;
	private String token;

	MaterialVisibility(int index, String tok) {
		this.index = index;
		this.token = tok;
	}

	/**
	 * @return index 0-2
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * @return string representation P/S/O
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * @param token
	 *            string representation.
	 * @return Enum from token.
	 */
	public static MaterialVisibility value(String token) {
		if ("O".equals(token)) {
			return Public;
		} else if ("S".equals(token)) {
			return Shared;
		}
		return Private;
	}

	/**
	 *
	 * @param index representation
	 * @return the corresponding enum.
	 */
	public static MaterialVisibility value(int index) {
		switch (index) {
		case 1:
			return MaterialVisibility.Shared;
		case 2:
			return MaterialVisibility.Public;
		case 0:
		default:
			return MaterialVisibility.Private;
		}
	}
}