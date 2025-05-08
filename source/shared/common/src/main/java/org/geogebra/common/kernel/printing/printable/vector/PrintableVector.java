package org.geogebra.common.kernel.printing.printable.vector;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * Printable vector.
 */
public interface PrintableVector {

	@MissingDoc
	ExpressionValue getX();

	@MissingDoc
	ExpressionValue getY();

	@MissingDoc
	ExpressionValue getZ();

	@MissingDoc
	boolean isCASVector();

	@MissingDoc
	int getCoordinateSystem();
}
