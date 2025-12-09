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
import org.geogebra.common.main.GeoGebraColorConstants;

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
			geo.setEuclidianVisible(true);
		}

		drawListAsComboBox(geo, value);
		geo.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	private void drawListAsComboBox(GeoList geo, boolean value) {
		// Set default object and background color if needed
		if (value && !geo.isBackgroundColorSet() && geo.isDefaultObjectColorSet()) {
			geo.setObjColor(GeoGebraColorConstants.NEUTRAL_900);
			geo.setBackgroundColor(geo.getBackgroundColor());
		}

		if (geo.getViewSet() == null) {
			app.getEuclidianView1().drawListAsComboBox(geo, value);
			return;
		}

		// #3929
		for (Integer view : geo.getViewSet()) {
			if (view == App.VIEW_EUCLIDIAN) {
				app.getEuclidianView1().drawListAsComboBox(geo, value);
			} else if (view == App.VIEW_EUCLIDIAN2
					&& app.hasEuclidianView2(1)) {
				app.getEuclidianView2(1).drawListAsComboBox(geo, value);
			}
		}
	}

}
