package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;

public class MatrixExpressionFilter implements ExpressionFilter {

	@Override
	public boolean isAllowed(ValidExpression expression) {
		return !containsMatrixExpression(expression);
	}

	private boolean containsMatrixExpression(ValidExpression expression) {
		return expression.inspect(subExpression -> {
			if (subExpression.evaluatesToList()) {
				ExpressionValue value = subExpression.evaluate(StringTemplate.defaultTemplate);
				return value != null && (value.unwrap() instanceof ListValue)
						&& ((ListValue) value.unwrap()).isMatrix();
			}
			return false;
		});
	}
}
