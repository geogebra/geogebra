package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;

/**
 * Class to check if expression has multiple variables
 */
class MultipleVariableChecker implements Inspecting {
	private int variables = 0;

	@Override
	public boolean check(ExpressionValue v) {
		if (v instanceof FunctionVariable) {
			variables++;
		}
		return variables > 1;
	}
}
