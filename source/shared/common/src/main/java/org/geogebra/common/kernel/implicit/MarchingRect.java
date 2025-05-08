package org.geogebra.common.kernel.implicit;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Rectangle for the marching square algorithm.
 */
public interface MarchingRect {
	@MissingDoc
	double x1();

	@MissingDoc
	double y1();

	@MissingDoc
	double x2();

	@MissingDoc
	double y2();

	@MissingDoc
	double topLeft();

	@MissingDoc
	double topRight();

	@MissingDoc
	double bottomLeft();

	@MissingDoc
	double bottomRight();

	/**
	 * @param i corner index
	 * @return corner evaluation
	 */
	double cornerAt(int i);
}
