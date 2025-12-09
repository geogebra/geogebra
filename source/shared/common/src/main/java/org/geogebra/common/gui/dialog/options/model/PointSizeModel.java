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

public class PointSizeModel extends SliderOptionsModel {

	public PointSizeModel(App app) {
		super(app);
	}

	private PointProperties getPointPropertiesAt(int index) {
		return (PointProperties) getObjectAt(index);
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return geo instanceof PointProperties
				&& ((PointProperties)geo).showPointProperties();
	}

	@Override
	public void apply(int index, int value) {
		PointProperties point = getPointPropertiesAt(index);
		point.setPointSize(value);
		((GeoElement) point).updateVisualStyle(GProperty.POINT_STYLE);
		point.updateRepaint();
	}

	@Override
	public int getValueAt(int index) {
		return getPointPropertiesAt(index).getPointSize();
	}

}
