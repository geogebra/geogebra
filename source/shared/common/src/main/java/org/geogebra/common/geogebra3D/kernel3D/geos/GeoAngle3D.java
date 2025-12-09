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

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.plugin.GeoClass;

/**
 * Angle that may or may not be oriented
 *
 */
final public class GeoAngle3D extends GeoAngle {
	private boolean hasOrientation;

	/**
	 * @param c
	 *            construction
	 */
	public GeoAngle3D(Construction c) {
		super(c);
		hasOrientation = false;
	}

	@Override
	public GeoAngle copy() {
		GeoAngle3D angle = new GeoAngle3D(cons);
		angle.hasOrientation = hasOrientation;
		copyPropertiesTo(angle);
		return angle;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.ANGLE3D;
	}

	@Override
	public boolean hasOrientation() {
		return hasOrientation; // no specific orientation
	}

	/**
	 * set if it has orientation
	 * 
	 * @param flag
	 *            flag
	 */
	public void setHasOrientation(boolean flag) {
		hasOrientation = flag;
	}

	/**
	 * create a new GeoAngle3D and set its interval (e.g. between 0 and 180
	 * degrees) as default angle
	 * 
	 * @param cons
	 *            construction
	 * @return new GeoAngle
	 */
	static public GeoAngle3D newAngle3DWithDefaultInterval(
			Construction cons) {
		GeoAngle3D ret = new GeoAngle3D(cons);
		// set the angle interval
		ret.setHasOrientation(true);
		ret.setDrawableNoSlider();
		ret.setAngleStyle(((GeoAngle) cons.getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_ANGLE))
						.getAngleStyle());
		return ret;
	}

	@Override
	public void setAngleStyle(AngleStyle angleStyle) {

		if (!hasOrientation() && (angleStyle == AngleStyle.ANTICLOCKWISE
				|| angleStyle == AngleStyle.UNBOUNDED)) {
			super.setAngleStyle(AngleStyle.NOTREFLEX);
		} else {
			super.setAngleStyle(angleStyle);
		}
	}
}
