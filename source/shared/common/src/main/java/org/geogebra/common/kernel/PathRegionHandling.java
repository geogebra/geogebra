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

package org.geogebra.common.kernel;

/**
 * Types of path / region parameter behaviours
 * 
 * @author Zbynek
 *
 */
public enum PathRegionHandling {
	/** use closest point when path / region changed */
	OFF("false"),
	/** use same parameter when path / region changed */
	ON("true"),
	/** try to be consistent with old behavior (3.2) */
	AUTO("auto");
	private final String xml;

	PathRegionHandling(String xml) {
		this.xml = xml;
	}

	/**
	 * @return value for xml
	 */
	public String getXML() {
		return xml;
	}

	/**
	 * @param s
	 *            XML string
	 * @return parsed value
	 */
	public static PathRegionHandling parse(String s) {
		if ("false".equals(s)) {
			return OFF;
		}
		return ON;
	}
}
