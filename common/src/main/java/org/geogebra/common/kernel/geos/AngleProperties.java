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

	public void setAllowReflexAngle(boolean parseBoolean);

	public void setEmphasizeRightAngle(boolean parseBoolean);

	public void setForceReflexAngle(boolean parseBoolean);

	public void setArcSize(int parseInt);

	public int getArcSize();

	public boolean isDrawable();

	public int getDecorationType();

	public void setDecorationType(int type);

	public boolean isEmphasizeRightAngle();

	public void setAngleStyle(int parseInt);


}
