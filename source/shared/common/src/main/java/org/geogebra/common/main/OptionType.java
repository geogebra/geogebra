package org.geogebra.common.main;

/**
 * Option panel types
 */
public enum OptionType {
	// Order matters for the selection menu. A separator is placed after
	// OBJECTS and SPREADSHEET to isolate the view options
	OBJECTS("Objects"), EUCLIDIAN("DrawingPad"), EUCLIDIAN2("DrawingPad2"),
	EUCLIDIAN_FOR_PLANE("ExtraViews"), EUCLIDIAN3D("GraphicsView3D"),
	CAS("CAS"), SPREADSHEET("Spreadsheet"), LAYOUT("Layout"),
	DEFAULTS("Defaults"), ALGEBRA("Algebra"), GLOBAL("Advanced");

	private final String transKey;

	OptionType(String transKey) {
		this.transKey = transKey;
	}

	public String getName() {
		return transKey;
	}
}