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
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

public class EnableDynamicCaptionModel extends BooleanOptionModel {

	public EnableDynamicCaptionModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	private GeoElementND at(int index) {
		return (GeoElementND) getObjectAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return at(index).hasDynamicCaption();
	}

	@Override
	public boolean isValidAt(int index) {
		return getObjectAt(index) instanceof GeoElementND;
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElementND asGeoText = at(index);
		if (value) {
			asGeoText.clearDynamicCaption();
		} else {
			asGeoText.removeDynamicCaption();
		}

		asGeoText.updateVisualStyleRepaint(GProperty.CAPTION);
	}

	@Override
	public String getTitle() {
		return "UseTextAsCaption";
	}
}
