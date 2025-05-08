package org.geogebra.common.kernel.arithmetic.filter;

import static org.geogebra.common.plugin.Operation.NO_OPERATION;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

/**
 * A filter for operations used to restrict expressions containing them (during exams).
 */
public interface OperationFilter {
	/**
	 * @param operation operation to be evaluated
	 * @return {@code true} if the operation is allowed, {@code false} otherwise
	 */
	boolean isAllowed(@Nonnull Operation operation);

	/**
	 * Converts this into an {@link ExpressionFilter} that filters nodes with operations
	 * that are restricted by this operation filter.
	 * @return an expression filter base on this operation filter
	 */
	default @Nonnull ExpressionFilter toExpressionFilter() {
		return this::isAllowed;
	}

	/**
	 * Creates an operation filter that restricts the set of operations passed as parameter.
	 * @param operations operations to restrict
	 * @return an operation filter
	 */
	static @Nonnull OperationFilter restricting(@Nonnull Set<Operation> operations) {
		return operation -> !operations.contains(operation);
	}

	private boolean isAllowed(@Nonnull ExpressionValue expressionValue) {
		if (!(expressionValue instanceof ExpressionNode)) {
			return true;
		}
		ExpressionNode expressionNode = (ExpressionNode) expressionValue;
		Operation operation = expressionNode.getOperation();
		return operation == NO_OPERATION || isAllowed(operation);
	}
}
