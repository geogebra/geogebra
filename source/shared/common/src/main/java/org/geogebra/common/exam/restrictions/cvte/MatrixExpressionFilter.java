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

package org.geogebra.common.exam.restrictions.cvte;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;

public class MatrixExpressionFilter implements ExpressionFilter {

	@Override
	public boolean isAllowed(@Nonnull ExpressionValue expression) {
		return !containsMatrixExpression(expression);
	}

	private boolean containsMatrixExpression(ExpressionValue expression) {
		return expression.any(subExpression -> {
			if (subExpression.evaluatesToList()) {
				ExpressionValue value = subExpression.evaluate(StringTemplate.defaultTemplate);
				return value != null && (value.unwrap() instanceof ListValue)
						&& ((ListValue) value.unwrap()).isMatrix();
			}
			return false;
		});
	}
}
