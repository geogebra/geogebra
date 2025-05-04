package org.geogebra.common.kernel.implicit;

/**
 * Rectangle for the marching square algorithm.
 */
public interface MarchingRect {
	double x1();

	double y1();

	double x2();

	double y2();

	double topLeft();

	double topRight();

	double bottomLeft();

	double bottomRight();

	/**
	 * @param i corner index
	 * @return corner evaluation
	 */
	double cornerAt(int i);
}
