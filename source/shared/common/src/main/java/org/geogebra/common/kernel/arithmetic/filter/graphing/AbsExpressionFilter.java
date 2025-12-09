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

package org.geogebra.common.kernel.arithmetic.filter.graphing;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionNodeOperationFilter;
import org.geogebra.common.plugin.Operation;

final public class AbsExpressionFilter extends ExpressionNodeOperationFilter {

	public AbsExpressionFilter() {
		super(Operation.ABS);
	}

	@Override
	protected boolean isExpressionNodeAllowedForOperation(@Nonnull ExpressionNode expression) {
		ExpressionValue left = expression.getLeft();
		return left.evaluatesToNumber(true) || left instanceof FunctionNVar;
	}
}
