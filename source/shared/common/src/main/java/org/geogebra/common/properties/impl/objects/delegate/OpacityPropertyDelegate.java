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

package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class OpacityPropertyDelegate extends AbstractGeoElementDelegate {

	public OpacityPropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof ChartStyleGeo) {
			return false;
		}
		if (isTextOrInput(element)) {
			return false;
		}
		if (element instanceof GeoButton) {
			return false;
		}
		if (element instanceof GeoList) {
			// check both isFillable (=not all children labeled)
			// and child applicability (e.g. does not contain buttons)
			return element.isFillable() && isApplicableToGeoList((GeoList) element);
		}
		return element.isFillable();
	}
}
