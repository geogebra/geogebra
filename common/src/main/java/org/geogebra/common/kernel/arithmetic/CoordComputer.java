package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.plugin.Operation;

public class CoordComputer implements Traversing {

	public ExpressionValue process(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			ExpressionNode en = ev.wrap();
			if (en.getOperation() == Operation.XCOORD) {
				return VectorArithmetic.computeCoord(en.getLeftTree(), 0);
			}
			if (en.getOperation() == Operation.YCOORD) {
				return VectorArithmetic.computeCoord(en.getLeftTree(), 1);
			}
			if (en.getOperation() == Operation.ZCOORD) {
				return VectorArithmetic.computeCoord(en.getLeftTree(), 2);
			}
		}
		return ev;
	}

}
