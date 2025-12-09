/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
