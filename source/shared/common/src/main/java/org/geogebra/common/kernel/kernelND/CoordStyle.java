package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.arithmetic.VectorNDValue;

/**
 * interface to handle coord style
 * 
 * @author mathieu
 *
 */
public interface CoordStyle extends VectorNDValue {

	/** set to 2D cartesian coords */
	public void setCartesian();

	/** set to polar coords */
	public void setPolar();

	/** set to complex coords */
	public void setComplex();

	/** set to 3D cartesian coords */
	public void setCartesian3D();

	/** set to spherical coords */
	public void setSpherical();

}
