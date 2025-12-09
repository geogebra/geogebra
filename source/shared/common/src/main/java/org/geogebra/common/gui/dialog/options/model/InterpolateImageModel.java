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

import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;

public class InterpolateImageModel extends BooleanOptionModel {

	public InterpolateImageModel(App app) {
		super(null, app);
	}

	private GeoImage getGeoImageAt(int index) {
		return (GeoImage) getObjectAt(index);
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoImageAt(index).isInterpolate();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoImage image = getGeoImageAt(index);
		image.setInterpolate(value);
		image.updateRepaint();
	}

	@Override
	public String getTitle() {
		return "Interpolate";
	}

	@Override
	protected boolean isValidAt(int index) {
		return getObjectAt(index) instanceof GeoImage;
	}

}
