package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.plugin.Operation;

/**
 * Checks and counts the given operator in the expression.
 */
public class OperationCountChecker implements Inspecting {
	private Operation operation;
	private int count = 0;

	/**
	 * @param operation to count.
	 */
	public OperationCountChecker(Operation operation) {
		this.operation = operation;
		this.count = 0;
	}

	@Override
	public boolean check(ExpressionValue v) {
		if (v.isOperation(operation)) {
			count++;
		}

		return false;
	}

	/**
	 *
	 * @return the actual count of operator
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Reset counter
	 */
	public void reset() {
		count = 0;
	}
}
