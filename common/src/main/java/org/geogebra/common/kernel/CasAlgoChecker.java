package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.DependentAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.plugin.Operation;

public class CasAlgoChecker implements Inspecting {

	@Override
	public boolean check(ExpressionValue v) {
		if (v.isOperation(Operation.DERIVATIVE)) {
			return true;
		}
		if (v.isOperation(Operation.EQUAL_BOOLEAN)
				|| v.isOperation(Operation.NOT_EQUAL)) {
			return ((ExpressionNode) v).getLeft() instanceof CasEvaluableFunction;
		}
		return false;
	}

	/**
	 * Detects algos that need recomputation on CAS reload
	 * @param algo algorithm
	 * @return whether the algorithm is depending on CAS
	 */
	public boolean isAlgoUsingCas(AlgoElement algo) {
		return algo instanceof UsesCAS || algo instanceof AlgoCasCellInterface
				|| (algo instanceof DependentAlgo
				&& (((DependentAlgo) algo).getExpression()).inspect(this));
	}
}
