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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.main.App;

public class DrawArrowsModel extends BooleanOptionModel {

	public DrawArrowsModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isSelectionAllowed(null);
	}

	@Override
	public void apply(int index, boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo instanceof GeoLocus) {
				((GeoLocus) geo).drawAsArrows(value);
				geo.updateVisualStyleRepaint(GProperty.DECORATION);
			}
		}
		storeUndoInfo();
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return geo instanceof GeoLocus && ((GeoLocus)geo).hasDrawArrows();
	}

	@Override
	public void updateProperties() {
		if (getListener() != null) {
			getListener().updateCheckbox(checkDrawArrow());
		}
	}

	@Override
	public String getTitle() {
		return "DrawArrows";
	}

	private boolean checkDrawArrow() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo instanceof GeoLocus) {
				return geo.isDrawArrows();
			}
		}
		return false;
	}

}
