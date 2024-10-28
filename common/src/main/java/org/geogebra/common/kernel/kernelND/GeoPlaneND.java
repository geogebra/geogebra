package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * simple plane interface for all geos that can be considered as a plane (3D
 * plane, polygons, ...)
 * 
 * @author mathieu
 *
 */
public interface GeoPlaneND extends GeoCoordSys2D, EquationValue {

	/**
	 * sets the fading for the "ends" of the plane
	 * 
	 * @param fading
	 *            fading
	 */
	public void setFading(float fading);

	/**
	 * 
	 * @return the fading for the "ends" of the plane
	 */
	public float getFading();

	/**
	 * create a 2D view of this plane
	 */
	public void createView2D();

	/**
	 * @param coords
	 *            point
	 * @return coords of normal projection of given point
	 */
	@Override
	public Coords[] getNormalProjection(Coords coords);

	/**
	 * @param h3d
	 *            other plane
	 * @return distance
	 */
	public double distanceWithSign(GeoPlaneND h3d);

	// TODO APPS-5867 change to setEquationForm(EquationForm.Linear equationForm)
	/**
	 * @param toStringMode
	 *            equation style
	 */
	public void setMode(int toStringMode);

	/**
	 * @return string mode
	 */
	public int getToStringMode();
}
