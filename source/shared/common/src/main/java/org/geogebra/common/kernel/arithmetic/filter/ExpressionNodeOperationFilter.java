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

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.plugin.Operation;

/**
 * Filters expression nodes based on Operation. If used with subclassing, special case criteria
 * can be implemented using {@link ExpressionNodeOperationFilter#isExpressionNodeAllowedForOperation(ExpressionNode)}
 * method. Otherwise, can be used as filtering for operations.
 */
public class ExpressionNodeOperationFilter extends ExpressionNodeFilter {

	final Operation operation;

	public ExpressionNodeOperationFilter(@Nonnull Operation operation) {
		this.operation = operation;
	}

	@Override
	protected boolean isExpressionNodeAllowed(@Nonnull ExpressionNode expressionNode) {
		return !expressionNode.isOperation(operation) || isExpressionNodeAllowedForOperation(
				expressionNode);
	}

	/**
	 * Checks if expression node with given operation is allowed. Consider this as a special case,
	 * where an operation is filtered only if some conditions are met.
	 * @param expression expression node
	 * @return true if operation is allowed. returns false by default.
	 */
	protected boolean isExpressionNodeAllowedForOperation(@Nonnull ExpressionNode expression) {
		return false;
	}
}
