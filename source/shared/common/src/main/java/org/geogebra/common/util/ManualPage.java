package org.geogebra.common.util;

/**
 * Manual pages and groups thereof.
 */
public enum ManualPage {
	/** Url for wiki article about functions */
	OPERATORS("Predefined_Functions_and_Operators"),
	/** Url for main page of manual */
	MAIN_PAGE(""),
	/** Url for wiki article about CAS */
	CAS_VIEW("CAS_View"),

	/** Url for wiki article about functions */
	TEXT_TOOL("tools/Text"),

	CONSTRUCTION_PROTOCOL("Construction_Protocol"),

	INPUT_BAR("Input_Bar"),

	FUNCTION_INSPECTOR_TOOL("tools/Function_Inspector"),
	/**
	 * Url for wiki article about exporting to HTML changed to GeoGebra
	 * Materials upload from ggb44
	 */
	EXPORT_WORKSHEET("Export_Worksheet_Dialog"), COMMAND(""), TOOL("");

	private final String url;

	ManualPage(String url) {
		this.url = url;
	}
	
	public String getURL() {
		return this.url;
	}
}
