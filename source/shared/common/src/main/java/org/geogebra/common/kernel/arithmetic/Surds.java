package org.geogebra.common.kernel.arithmetic;

import static org.apache.commons.math3.primes.Primes.primeFactors;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.Operation;

public class Surds {

	protected static ExpressionValue getResolution(ExpressionNode expr,
			Kernel kernel) {
		ExpressionValue left = expr.getLeft();
		Operation op = expr.getOperation();
		ExpressionValue evaluated = left.evaluate(StringTemplate.defaultTemplate).unwrap();
		if (evaluated instanceof NumberValue && op == Operation.SQRT) {
			// Sqrt of number
			NumberValue number = (NumberValue) evaluated;
			double value = number.getDouble();
			if (value % 1 == 0 && value > 1 && value < Integer.MAX_VALUE) {
				return getSimplifiedSurd(kernel, (int) value);
			}
		}
		return null;
	}

	private static ExpressionValue getSimplifiedSurd(Kernel kernel, int value) {
		List<Integer> factors = primeFactors((int) value);
		int outerValue = 1;
		int innerValue = 1;

		int currentValue = Integer.MIN_VALUE;
		for (Integer factor : factors) {
			if (currentValue != factor) {
				if (currentValue != Integer.MIN_VALUE) {
					innerValue *= currentValue;
				}
				currentValue = factor;
			} else {
				outerValue *= factor;
				currentValue = Integer.MIN_VALUE;
			}
		}
		if (currentValue != Integer.MIN_VALUE) {
			innerValue *= currentValue;
		}
		if (innerValue == 1 || outerValue == 1) {
			return null;
		}
		ExpressionValue innerLeft = new MyDouble(kernel, innerValue);
		ExpressionValue sqrt = new ExpressionNode(kernel, innerLeft, Operation.SQRT, null);
		return new ExpressionNode(kernel, new MyDouble(kernel, outerValue), Operation.MULTIPLY,
				sqrt);
	}
}
