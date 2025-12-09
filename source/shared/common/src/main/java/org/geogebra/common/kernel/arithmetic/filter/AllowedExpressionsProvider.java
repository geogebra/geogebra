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

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * Provides a collection of allowed expressions based on the
 * current expression value for a {@link DeepExpressionFilter}.
 * At every step of the expression node tree traversal, {@link DeepExpressionFilter} asks this
 * to provider expressions that are allowed, even if otherwise would be restricted by the filter.
 */
@FunctionalInterface
public interface AllowedExpressionsProvider {

	/**
	 * Provides the allowed child of value to bypass restrictions.
	 * @param value expression value provide allowed values from
	 * @return Optionally a collection of values that are allowed
	 */
	@CheckForNull Collection<ExpressionValue> provideAllowedExpressionValues(@Nonnull ExpressionValue value);
}
