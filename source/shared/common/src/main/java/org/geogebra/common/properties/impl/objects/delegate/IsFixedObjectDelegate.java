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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.settings.AlgebraSettings;

public class IsFixedObjectDelegate extends AbstractGeoElementDelegate {

	public IsFixedObjectDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		if (element instanceof GeoList) {
			return isApplicableToList((GeoList) element);
		}
		AlgebraSettings algebraSettings = element.getApp().getSettings().getAlgebra();
		if (hasFunctionProperties(element) && algebraSettings.isEquationChangeByDragRestricted()) {
			return false;
		}
		return element.showFixUnfix();
	}

	private boolean hasFunctionProperties(GeoElement element) {
		if (element instanceof GeoList && !checkIsApplicable(element)) {
			return false;
		} else {
			return element.isFunctionOrEquationFromUser();
		}
	}

	private boolean isApplicableToList(GeoList list) {
		GeoElement elementForProperties = list.getGeoElementForPropertiesDialog();
		return elementForProperties instanceof GeoFunction;
	}
}
