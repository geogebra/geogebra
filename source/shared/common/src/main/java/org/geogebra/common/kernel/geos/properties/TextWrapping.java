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

package org.geogebra.common.kernel.geos.properties;

import java.util.Locale;

/**
 * Text wrapping for tables.
 */
public enum TextWrapping {
	WRAP, CLIP;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}

	/**
	 * @param wrapping string from XML
	 * @return wrapping value
	 */
	public static TextWrapping fromString(String wrapping) {
		try {
			return valueOf(wrapping.toUpperCase(Locale.US));
		} catch (RuntimeException e) {
			// Invalid wrapping
			return null;
		}
	}
}
