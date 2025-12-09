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

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

public class TableProperties {

	/**
	 * Copy table view settings from target to
	 * 
	 * @param fromGeo
	 *            source element
	 * @param to
	 *            target function
	 */
	public static void transfer(GeoElementND fromGeo, GeoEvaluatable to) {
		if (fromGeo instanceof GeoEvaluatable) {
			GeoEvaluatable from = (GeoEvaluatable) fromGeo;
			to.setTableColumn(from.getTableColumn());
			to.setPointsVisible(from.isPointsVisible());
		}
	}

}
