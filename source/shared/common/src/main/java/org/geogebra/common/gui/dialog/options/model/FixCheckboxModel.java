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

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.main.App;

public class FixCheckboxModel extends BooleanOptionModel {

	public FixCheckboxModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	private GeoBoolean getBooleanAt(int index) {
		return (GeoBoolean) getObjectAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return getBooleanAt(index).isLockedPosition();
	}

	@Override
	public boolean isValidAt(int index) {
		Object geo = getObjectAt(index);
		if (geo instanceof GeoBoolean) {
			GeoBoolean bool = (GeoBoolean) geo;
			if (!bool.isIndependent()) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public void apply(int index, boolean value) {
		GeoBoolean bool = getBooleanAt(index);
		bool.setCheckboxFixed(value);
		bool.updateRepaint();
	}

	@Override
	public String getTitle() {
		return "FixCheckbox";
	}
}
