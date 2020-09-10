package org.geogebra.common.kernel.geos.properties;

public enum BorderType {

	ALL("all"),

	INNER("inner"),

	OUTER("outer"),

	NONE("none"),

	MIXED("mixed");

	private String borderType;

	BorderType(String borderType) {
		this.borderType = borderType;
	}

	/**
	 * @return border style name
	 */
	public String getName() {
		return borderType;
	}
}
