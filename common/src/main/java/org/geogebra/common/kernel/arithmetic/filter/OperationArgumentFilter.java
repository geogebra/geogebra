package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

/**
 * Filters operation.
 */
public interface OperationArgumentFilter {

	/**
	 * Return true if the operation is allowed with the specified arguments.
	 *
	 * @param operation operation
	 * @param left left argument
	 * @param right right argument
	 * @return true iff the operation is allowed
	 */
	boolean isAllowed(Operation operation, ExpressionValue left, ExpressionValue right);
}
