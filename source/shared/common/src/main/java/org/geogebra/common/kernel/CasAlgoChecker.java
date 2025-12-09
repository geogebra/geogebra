/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.DependentAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
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
					&& hasExpressionWithCasOperations((DependentAlgo) algo))
				|| isFunctionEqualityCheck(algo);
	}

	private boolean isFunctionEqualityCheck(AlgoElement algo) {
		return algo.getClassName() == Commands.AreEqual
				&& algo.getInput(0) instanceof CasEvaluableFunction;
	}

	private boolean hasExpressionWithCasOperations(DependentAlgo algo) {
		return algo.getExpression() != null && algo.getExpression().any(this);
	}
}
