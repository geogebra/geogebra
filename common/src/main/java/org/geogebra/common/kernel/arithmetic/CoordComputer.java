package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.plugin.Operation;

/**
 * Replaces xcoord nodes by the expanded version, eg
 * 
 * x(A+B+(1,0)) -&gt; x(A)+x(B)+1
 * 
 * @author Zbynek
 *
 */
public class CoordComputer implements Traversing {

	@Override
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
