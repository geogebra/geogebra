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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Allow GeoList to have angle properties so that eg can change angles
 * in a list to be all reflex.
 * @author michael
 */
public interface AngleProperties extends GeoElementND {

	/**
	 * Returns angle style. See GeoAngle.ANGLE_*
	 * 
	 * @return anticlockwise, reflex, not reflex or unbounded
	 */
	AngleStyle getAngleStyle();

	/**
	 * 
	 * @return true if has a "super" orientation (e.g. in 3D, from a specific
	 *         oriented plane)
	 */
	boolean hasOrientation();

	/**
	 * Changes angle style and recomputes the value from raw. See
	 * GeoAngle.ANGLE_*
	 * 
	 * @param angleStyle
	 *            clockwise, anticlockwise, (force) reflex or (force) not reflex
	 */
	void setAngleStyle(AngleStyle angleStyle);

	/**
	 * @param allowReflex
	 *            whether reflex angle is allowed
	 */
	void setAllowReflexAngle(boolean allowReflex);

	/**
	 * @param emRightAngle
	 *            whether to use special EV drawing when this angle is right
	 */
	void setEmphasizeRightAngle(boolean emRightAngle);

	/**
	 * @param forceReflex
	 *            whether angle is forced to (180,360)
	 */
	void setForceReflexAngle(boolean forceReflex);

	/**
	 * @param arcSize
	 *            arc size
	 */
	void setArcSize(int arcSize);

	/**
	 * @return arc radius
	 */
	int getArcSize();

	/**
	 * @param type
	 *            decoration
	 */
	void setDecorationType(int type);

	/**
	 * @return whether right angle is drawn differently
	 */
	boolean isEmphasizeRightAngle();

	/**
	 * @param angleStyle
	 *            see AngleStyle enum in GeoAngle.
	 */
	void setAngleStyle(int angleStyle);

}
