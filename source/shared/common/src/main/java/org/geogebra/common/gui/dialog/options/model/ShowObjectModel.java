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

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class ShowObjectModel extends BooleanOptionModel {
	public interface IShowObjectListener extends IBooleanOptionListener {
		@MissingDoc
		void updateCheckbox(boolean value, boolean isEnabled);

	}

	public ShowObjectModel(IShowObjectListener listener, App app) {
		super(listener, app);
	}

	@Override
	public void updateProperties() {
		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalObjectVal = true;
		boolean showObjectCondition = geo0.getShowObjectCondition() != null;

		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object visible value
			if (geo0.isSetEuclidianVisible() != temp.isSetEuclidianVisible()) {
				equalObjectVal = false;
				break;
			}

			if (temp.getShowObjectCondition() != null) {
				showObjectCondition = true;
			}
		}

		((IShowObjectListener) getListener()).updateCheckbox(equalObjectVal
				&& geo0.isSetEuclidianVisible(),
				!showObjectCondition);

	}

	@Override
	public String getTitle() {
		return "ShowObject";
	}

	@Override
	public boolean isValidAt(int index) {
		boolean isValid = true;
		GeoElement geo = getGeoAt(index);
		if (!geo.isEuclidianToggleable()
				// can't allow a free fixed number to become visible (as a
				// slider)
				|| (geo.isGeoNumeric() && geo.isLocked())) {
			isValid = false;

		}

		return isValid;
	}

	@Override
	public boolean getValueAt(int index) {
		// not used
		return false;
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setEuclidianVisible(value);
		geo.updateVisualStyleRepaint(GProperty.VISIBLE);
		storeUndoInfo();
	}

}