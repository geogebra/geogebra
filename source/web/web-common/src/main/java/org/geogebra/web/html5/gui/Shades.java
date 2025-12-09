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

package org.geogebra.web.html5.gui;

/**
 * Shades of the neutral color.
 */
public enum Shades {
	NEUTRAL_0("neutral-0"),
	NEUTRAL_100("neutral-100"),
	NEUTRAL_200("neutral-200"),
	NEUTRAL_300("neutral-300"),
	NEUTRAL_400("neutral-400"),
	NEUTRAL_500("neutral-500"),
	NEUTRAL_600("neutral-600"),
	NEUTRAL_700("neutral-700"),
	NEUTRAL_800("neutral-800"),
	NEUTRAL_900("neutral-900");

	public final String name;

	Shades(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getFgColName() {
		return "fg-" + getName();
	}
}