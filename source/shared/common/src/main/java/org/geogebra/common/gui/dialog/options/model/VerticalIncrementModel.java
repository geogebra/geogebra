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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

public class VerticalIncrementModel extends TextPropertyModel{

	public VerticalIncrementModel(App app) {
		super(app);
	}

	@Override
	protected boolean isValidAt(int index) {
		return getGeoAt(index) != null && getGeoAt(index).isGeoPoint();
	}

	@Override
	public String getText() {
		String val = null;
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo.isGeoPoint()) {
				NumberValue step = ((GeoPointND) geo).getVerticalIncrement();
				if (step == null) {
					step = geo.getAnimationStepObject();
				}
				String current = step == null ? "" : step.getLabel(StringTemplate.editTemplate);
				if (val == null) {
					val = current;
				} else if (!val.equals(current)) {
					val = "";
				}
			}
		}
		return val;
	}

	@Override
	protected void applyChanges(GeoNumberValue val, String text) {
		for (int i = 0; i < getGeosLength(); i++) {
			((GeoPointND) getGeoAt(i)).setVerticalIncrement(val);
		}
		storeUndoInfo();
	}

	@Override
	public String getTitle() {
		return "IncrementVertical";
	}
}
