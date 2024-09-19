package org.geogebra.common.kernel.interval.node;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.plugin.Operation;

public class IntervalOperationSupport {
	private final Map<Operation, IntervalOperation> operationMap = new HashMap<>();

	/**
	 * Constructor
	 */
	public IntervalOperationSupport() {
		for (IntervalOperation operation: IntervalOperation.values()) {
			if (operation != IntervalOperation.UNSUPPORTED) {
				operationMap.put(operation.mappedOperation(), operation);
			}
			operationMap.put(Operation.INVISIBLE_PLUS, IntervalOperation.PLUS);
		}
	}

	/**
	 *
	 * @param operation supported by ExpressionNode.
	 *
	 * @return if an equivalent operation is supported by IntervalExpressionNode.
	 * .
	 */
	public boolean isSupported(Operation operation) {
		return operationMap.containsKey(operation);
	}

	/**
	 *
	 * @param operation supported by ExpressionNode.
	 *
	 * @return the equivalent operation that is supported by IntervalExpressionNode.
	 * .
	 */
	public IntervalOperation convert(Operation operation) {
		return operationMap.getOrDefault(operation, IntervalOperation.UNSUPPORTED);
	}
}
