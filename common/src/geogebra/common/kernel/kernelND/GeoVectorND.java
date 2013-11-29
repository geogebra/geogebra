package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.Matrix.Coords;

/**
 * Simple common interface for GeoVector and GeoVector3D
 * 
 * @author ggb3D
 *
 */
public interface GeoVectorND extends GeoDirectionND, Locateable {

	void setLabel(String label);

	void setUndefined();
	
	/**
	 * @param c coordinates as array
	 */
	public void setCoords(double[] c);
	
	/**
	 * @param dimension dimension of desired coordinates
	 * @return the coords of the vector in the given dimension (extended or projected)
	 */
	public Coords getCoordsInD(int dimension);

	/**
	 * UPdates start point
	 */
	void updateStartPointPosition();

	/**
	 * @return true if all coords are finite
	 */
	boolean isFinite();

	/**
	 * @param coords array to store inhomogeneous coords
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

	
}
