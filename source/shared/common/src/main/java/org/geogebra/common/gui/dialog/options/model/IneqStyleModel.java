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

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.InequalityProperties;
import org.geogebra.common.main.App;

public class IneqStyleModel extends BooleanOptionModel {

	public IneqStyleModel(App app) {
		super(null, app);
	}

	private InequalityProperties getInequalityPropertiesAt(int index) {
		return (InequalityProperties) getObjectAt(index);
	}

	@Override
	public void updateProperties() {

		if (!(getObjectAt(0) instanceof InequalityProperties)) {
			return;
		}

		InequalityProperties temp, geo0 = getInequalityPropertiesAt(0);
		boolean equalFix = true;

		for (int i = 0; i < getGeosLength(); i++) {
			if (!(getObjectAt(i) instanceof InequalityProperties)) {
				return;
			}
			temp = getInequalityPropertiesAt(i);

			if (geo0.showOnAxis() != temp.showOnAxis()) {
				equalFix = false;
			}
		}

		if (equalFix) {
			getListener().updateCheckbox(geo0.showOnAxis());
		} else {
			getListener().updateCheckbox(false);
		}

	}

	@Override
	public String getTitle() {
		return "ShowOnXAxis";
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index).getGeoElementForPropertiesDialog();
		if (!(geo instanceof GeoFunction)) {
			return false;
		}

		GeoFunction gfun = (GeoFunction) geo;
		return gfun.isBooleanFunction() && !gfun
				.getVarString(StringTemplate.defaultTemplate).equals("y");
	}

	@Override
	public boolean getValueAt(int index) {
		// not used as updateProperties is overridden.
		return false;
	}

	@Override
	public void apply(int index, boolean value) {
		InequalityProperties geo = (InequalityProperties) getObjectAt(index);
		geo.setShowOnAxis(value);
		geo.updateRepaint();

	}

}
