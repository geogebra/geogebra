package org.geogebra.common.kernel.printing.printable.vector;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;

public interface PrintableVector {

    ExpressionValue getX();

    ExpressionValue getY();

    ExpressionValue getZ();

    boolean isCASVector();

    int getCoordinateSystem();
}
