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

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class RightAngleModel extends BooleanOptionModel {

	public RightAngleModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public void updateProperties() {
		AngleProperties geo0 = (AngleProperties) getGeoAt(0);
		getListener().updateCheckbox(geo0.isEmphasizeRightAngle());
	}

	@Override
	public String getTitle() {
		return "EmphasizeRightAngle";
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);

		return geo instanceof AngleProperties && !geo.isGeoList()
				|| isAngleList(geo);
	}

	@Override
	public boolean getValueAt(int index) {
		// not used here, as updateProperties is overridden.
		return false;
	}

	@Override
	public void apply(int index, boolean value) {
		AngleProperties geo = (AngleProperties) getObjectAt(index);
		geo.setEmphasizeRightAngle(value);
		geo.updateVisualStyle(GProperty.ANGLE_STYLE);
	}

}
