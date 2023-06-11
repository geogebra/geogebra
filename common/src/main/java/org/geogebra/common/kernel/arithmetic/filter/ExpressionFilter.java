package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.plugin.Operation;

/**
 * Filters entire operation.
 * For specific arguments only, see {@link OperationArgumentFilter}
 */
public interface ExpressionFilter {

	/**
	 * @param operation to check
	 * @return if allowed.
	 */
	boolean isAllowed(Operation operation);
}
