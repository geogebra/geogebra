package org.geogebra.common.kernel.arithmetic;

import java.util.List;

import org.geogebra.common.plugin.Operation;

/**
 * Checks and counts the given operator in the expression.
 */
public class OperationCountChecker implements Inspecting {
	private List<Operation> operations;
	private int count = 0;

	/**
	 * @param ops to count.
	 */
	public OperationCountChecker(Operation... ops) {
		this.operations = List.of(ops);
		this.count = 0;
	}

	@Override
	public boolean check(ExpressionValue v) {
		if (operations.contains(v.wrap().getOperation())) {
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
