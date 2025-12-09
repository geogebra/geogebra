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
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * Base class for filtering expression nodes based on some criteria.
 * Look into {@link DeepExpressionFilter} to filter at every level
 * of an expression tree.
 */
public abstract class ExpressionNodeFilter implements ExpressionFilter {
	@Override
	final public boolean isAllowed(@Nonnull ExpressionValue expression) {
		if (!expression.isExpressionNode()) {
			return true;
		}
		return isExpressionNodeAllowed((ExpressionNode) expression);
	}

	/**
	 * Checks whether an expression node is allowed.
	 *
	 * @param expressionNode expression node to test
	 * @return true if expression node is allowed
	 */
	protected abstract boolean isExpressionNodeAllowed(@Nonnull ExpressionNode expressionNode);
}
