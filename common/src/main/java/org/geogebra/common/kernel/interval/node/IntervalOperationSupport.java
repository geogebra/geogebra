package org.geogebra.common.kernel.interval.node;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.plugin.Operation;

public class IntervalOperationSupport {
	private final Map<Operation, IntervalOperation> operationMap = new HashMap<>();

	public IntervalOperationSupport() {
		for (IntervalOperation operation: IntervalOperation.values()) {
			if (operation != IntervalOperation.UNSUPPORTED) {
				operationMap.put(operation.op(), operation);
			}
		}
	}

	public boolean isSupported(Operation operation) {
		return operationMap.containsKey(operation);
	}

	public IntervalOperation convert(Operation operation) {
		return operationMap.getOrDefault(operation, IntervalOperation.UNSUPPORTED);
	}
}
