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

import java.util.Comparator;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Compares GeoElements by name and description alphabetically
 * 
 * @version 2010-06-14 Last change: generic Object replaced by GeoElement
 *          (Zbynek Konecny)
 */
public class NameDescriptionComparator implements Comparator<GeoElement> {
	@Override
	public int compare(GeoElement geo1, GeoElement geo2) {
		if (geo1 == null) {
			return -1;
		} else if (geo2 == null) {
			return 1;
		} else {
			return geo1.getNameDescription()
					.compareTo(geo2.getNameDescription());
		}
	}
}
