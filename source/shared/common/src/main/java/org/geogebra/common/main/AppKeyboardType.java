package org.geogebra.common.main;

/**
 * Keyboard type.
 */
public enum AppKeyboardType {
	SCIENTIFIC,

	NOTES,

	GRAPHING,

	GEOMETRY,

	SUITE,

	SOLVER;

	/**
	 * @param setting external name
	 * @return keyboard type
	 */
	public static AppKeyboardType fromName(String setting) {
		switch (setting) {
		case "normal":
			return AppKeyboardType.SUITE;
		case "notes":
			return AppKeyboardType.NOTES;
		case "solver":
			return AppKeyboardType.SOLVER;
		default:
			return AppKeyboardType.SCIENTIFIC;
		}
	}
}
