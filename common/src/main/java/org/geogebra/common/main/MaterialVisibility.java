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
}