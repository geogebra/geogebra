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
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;

public class PointStyleModel extends NumberOptionsModel {
	private IComboListener listener;

	public PointStyleModel(App app) {
		super(app);
	}

	private PointProperties getPointPropertiesAt(int index) {
		return (PointProperties) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		if (!hasGeos()) {
			return;
		}

		PointProperties geo0 = getPointPropertiesAt(0);
		if (listener == null) {
			return;
		}

		if ((geo0 == null) || (geo0.getPointStyle() == -1)) {
			// select default button
			listener.setSelectedIndex(EuclidianStyleConstants.POINT_STYLE_DOT);

		} else {
			// select custom button and set combo box selection
			listener.setSelectedIndex(geo0.getPointStyle());
		}
	}

	@Override
	public boolean checkGeos() {
		if (!hasGeos()) {
			return false;
		}
		return super.checkGeos();
	}

	@Override
	public boolean isValidAt(int index) {
		return match(getGeoAt(index));
	}

	/**
	 * Decides if geo is accepted for this model.
	 * 
	 * @param geo
	 *            The geo to match.
	 * @return if geo has point properties
	 */
	public static boolean match(GeoElement geo) {
		return geo instanceof PointProperties && ((PointProperties)geo).showPointProperties()
				&& !geo.isGeoElement3D();
	}

	public boolean is3D() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!(geo.isGeoPoint() && geo.isGeoElement3D())) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void apply(int index, int value) {
		PointProperties point = getPointPropertiesAt(index);
		point.setPointStyle(value);
		((GeoElement) point).updateVisualStyle(GProperty.POINT_STYLE);
		point.updateRepaint();
	}

	@Override
	protected int getValueAt(int index) {
		// not used
		return 0;// getPointPropertiesAt(index).getPointStyle();
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	public void setListener(IComboListener listener) {
		this.listener = listener;

	}

}
