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