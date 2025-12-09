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
