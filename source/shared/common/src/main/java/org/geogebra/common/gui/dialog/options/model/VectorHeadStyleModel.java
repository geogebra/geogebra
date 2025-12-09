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
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.VectorHeadStyle;
import org.geogebra.common.main.App;

public class VectorHeadStyleModel extends IconOptionsModel {
	private IComboListener listener;

	public VectorHeadStyleModel(App app) {
		super(app);
	}

	@Override
	public void apply(int index, int value) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoVector) {
			((GeoVector) geo).setHeadStyle(VectorHeadStyle.values()[value]);
			geo.updateVisualStyleRepaint(GProperty.COMBINED);
			app.updateStyleBars();
		}
	}

	@Override
	public void setListener(IComboListener listener) {
		this.listener = listener;
	}

	@Override
	public String getTitle() {
		return "Properties.Style";
	}

	@Override
	protected int getValueAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof GeoVector) {
			return ((GeoVector) geo).getHeadStyle().ordinal();
		}
		return 0;
	}

	@Override
	protected boolean isValidAt(int index) {
		return getGeoAt(index) instanceof GeoVector;
	}

	@Override
	public void updateProperties() {
		GeoElement geo = getGeoAt(0);
		if (geo instanceof GeoVector) {
			VectorHeadStyle style = ((GeoVector) geo).getHeadStyle();
			listener.setSelectedIndex(style.ordinal());
		}
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
