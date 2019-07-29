package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;

/**
 * Simple common interface for GeoVector and GeoVector3D
 * 
 * @author ggb3D
 *
 */
public interface GeoVectorND
		extends GeoDirectionND, Locateable, CoordStyle, VectorNDValue {

	/**
	 * @param c
	 *            coordinates as array
	 */
	public void setCoords(double[] c);

	/**
	 * @return the coords of the vector in 2D
	 */
	public Coords getCoordsInD2();

	/**
	 * @return the coords of the vector in 3D
	 */
	public Coords getCoordsInD3();

	/**
	 * UPdates start point
	 */
	void updateStartPointPosition();

	/**
	 * @return true if all coords are finite
	 */
	boolean isFinite();

	/**
	 * @param coords
	 *            array to store inhomogeneous coords
	 */
	void getInhomCoords(double[] coords);

	/**
	 * @return inhomogeneous coords
	 */
	double[] getInhomCoords();

	/**
	 * @return true if tracing
	 */
	public boolean getTrace();

	/**
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void setCoords(double x, double y, double z);

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param w
	 *            inhomogenous w for 3D vectors
	 */
	public void setCoords(double x, double y, double z, double w);

	/**
	 * 
	 * @return x component
	 */
	public double getX();

	/**
	 * 
	 * @return y component
	 */
	public double getY();

	/**
	 * 
	 * @return z component
	 */
	public double getZ();

}
