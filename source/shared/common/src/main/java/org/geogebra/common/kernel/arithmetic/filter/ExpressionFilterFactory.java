package org.geogebra.common.kernel.arithmetic.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.plugin.Operation;

/**
 * Factory that creates a expression filters.
 */
final public class ExpressionFilterFactory {

	private ExpressionFilterFactory() {
		// Factory class
	}

	/**
	 * Create a new ExpressionFilter that filters a set of operations.
	 * @param operations set of operations to filter
	 * @return expression filter
	 */
	public static ExpressionFilter createOperationsExpressionFilter(Set<Operation> operations) {
		return createOperationsExpressionFilter(operations.toArray(new Operation[0]));
	}

	/**
	 * Create a new ExpressionFilter that filters an array of operations.
	 * @param filteredOperations operations to filter
	 * @return expression filter
	 */
	public static ExpressionFilter createOperationsExpressionFilter(
			Operation... filteredOperations) {
		List<ExpressionFilter> operationFilters =
				Arrays.stream(filteredOperations).map(ExpressionNodeOperationFilter::new)
						.collect(Collectors.toList());
		ExpressionFilter combinedFilter = new CompositeExpressionFilter(operationFilters);
		return new InspectingExpressionFilter(combinedFilter);
	}
}
