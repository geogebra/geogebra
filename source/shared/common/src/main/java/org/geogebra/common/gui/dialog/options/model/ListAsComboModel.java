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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.impl.objects.ListAsComboBoxProperty;

public class ListAsComboModel extends BooleanOptionModel {

	public ListAsComboModel(App app, IBooleanOptionListener listener) {
		super(listener, app);
	}

	private GeoList getGeoListAt(int index) {
		return (GeoList) getObjectAt(index);
	}

	@Override
	public void applyChanges(boolean value) {
		super.applyChanges(value);
		app.refreshViews();
	}

	@Override
	public String getTitle() {
		return "DrawAsDropDownList";
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isGeoList();
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoListAt(0).drawAsComboBox();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoList geo = getGeoListAt(index);
		geo.setDrawAsComboBox(value);
		if (value) {
			ListAsComboBoxProperty.initializeVisualProperties(geo);
		}
		geo.updateVisualStyleRepaint(GProperty.COMBINED);
	}

}
