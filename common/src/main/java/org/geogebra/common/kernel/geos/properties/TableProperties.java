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
