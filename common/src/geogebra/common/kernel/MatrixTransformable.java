package geogebra.common.kernel;

import geogebra.common.kernel.kernelND.GeoElementND;

/**
 * GeoElement which supports matrix transformations
 */
public interface MatrixTransformable extends GeoElementND{
	/**
	 * Transforms the object using the matrix
	 * a00 a01
	 * a10 a11
	 * @param a00 a00
	 * @param a01 a01
	 * @param a10 a10
	 * @param a11 a11
	 */
	public void matrixTransform(double a00, double a01, double a10, double a11);

	/**
	 * Transforms the object using the matrix
	 * a00 a01 a02
	 * a10 a11 a12
	 * a20 a21 a22
	 * @param a00 a00
	 * @param a01 a01
	 * @param a02 a02
	 * @param a10 a10
	 * @param a11 a11
	 * @param a12 a12
	 * @param a20 a20
	 * @param a21 a21
	 * @param a22 a22
	 */
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22);

}
