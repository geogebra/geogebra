package org.geogebra.common.kernel.arithmetic.filter;

import java.util.Set;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

public class OperationExpressionFilter implements ExpressionFilter {

	private final Set<Operation> filteredOperations;

	public OperationExpressionFilter(Operation... filteredOperations) {
		this.filteredOperations = Set.of(filteredOperations);
	}

	private boolean isFilteredOperations(ExpressionValue expressionValue) {
		return filteredOperations.stream()
				.filter(operation -> expressionValue.isOperation(operation))
				.findFirst()
				.isPresent();
	}

	@Override
	public boolean isAllowed(ExpressionNode expression) {
		boolean containsFilteredOperations = expression.inspect(expressionValue -> isFilteredOperations(expressionValue));
		return !containsFilteredOperations;
	}
}
