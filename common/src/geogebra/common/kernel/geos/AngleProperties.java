package geogebra.common.kernel.geos;

/**
 * @author michael
 * 
 * Allow GeoList to have angle properties so that
 * eg can change angles in a list to be all reflex
 *
 */
public interface AngleProperties {

	
	/**
	 * @param index index of currently used interval
	 */
	public void setAngleInterval(int index);
	
	/**
	 * @return index of currently used interval
	 */
	public int getAngleInterval();

	/**
	 * Returns angle style. See GeoAngle.ANGLE_*
	 * 
	 * @return Clockwise, counterclockwise reflex or not reflex
	 */
	public int getAngleStyle();

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
	public void setAngleStyle(int angleStyle);

	public void setAllowReflexAngle(boolean parseBoolean);

	public void setEmphasizeRightAngle(boolean parseBoolean);

	public void setForceReflexAngle(boolean parseBoolean);

	public void setArcSize(int parseInt);

	public int getArcSize();

	public boolean isIndependent();

	public boolean isDrawable();

	public void updateRepaint();

	public int getDecorationType();

	public void setDecorationType(int type);

	public boolean isEmphasizeRightAngle();


}
