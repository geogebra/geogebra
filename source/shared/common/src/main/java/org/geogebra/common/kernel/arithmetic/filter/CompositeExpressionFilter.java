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

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * Combines a list of filters into a single ExpressionFilter.
 */
final public class CompositeExpressionFilter implements ExpressionFilter {

	final private List<ExpressionFilter> filters;

	/**
	 * Creates a new CompositeExpressionFilter
	 * @param filters expression filters to combine
	 */
	public CompositeExpressionFilter(@Nonnull List<ExpressionFilter> filters) {
		this.filters = filters;
	}

	@Override
	public boolean isAllowed(@Nonnull ExpressionValue expression) {
		return filters.stream().allMatch(filter -> filter.isAllowed(expression));
	}
}
