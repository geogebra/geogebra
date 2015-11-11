package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * @author michael
 * 
 * Allow GeoList to have angle properties so that
 * eg can change angles in a list to be all reflex
 *
 */
public interface AngleProperties extends GeoElementND{

	
	/**
	 * Returns angle style. See GeoAngle.ANGLE_*
	 * 
	 * @return anticlockwise, reflex, not reflex or unbounded
	 */
	public AngleStyle getAngleStyle();

	/**
	 * 
	 * @return true if has a "super" orientation (e.g. in 3D, from a specific oriented plane)
	 */
	public boolean hasOrientation();

	/**
	 * Changes angle style and recomputes the value from raw.
	 * See GeoAngle.ANGLE_*
	 * @param angleStyle clockwise, anticlockwise, (force) reflex or (force) not reflex
	 */
	public void setAngleStyle(AngleStyle angleStyle);

	/**
	 * @param allowReflex
	 *            whether reflex angle is allowed
	 */
	public void setAllowReflexAngle(boolean allowReflex);

	/**
	 * @param emRightAngle
	 *            whether to use special EV drawing when this angle is right
	 */
	public void setEmphasizeRightAngle(boolean emRightAngle);

	/**
	 * @param forceReflex
	 *            whether angle is forced to (180,360)
	 */
	public void setForceReflexAngle(boolean forceReflex);

	/**
	 * @param arcSize
	 *            arc size
	 */
	public void setArcSize(int arcSize);

	/**
	 * @return arc radius
	 */
	public int getArcSize();

	public boolean isDrawable();

	/**
	 * @return decoration
	 */
	public int getDecorationType();

	/**
	 * @param type
	 *            decoration
	 */
	public void setDecorationType(int type);

	/**
	 * @return whether right angle is drawn differently
	 */
	public boolean isEmphasizeRightAngle();

	/**
	 * @param angleStyle
	 *            see AngleStyle enum in GeoAngle.
	 */
	public void setAngleStyle(int angleStyle);


}
