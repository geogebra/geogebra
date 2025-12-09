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

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.filter.CompositeExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.DeepExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;

/**
 * ExpressionFilter factory for the Graphing app.
 */
final public class GraphingExpressionFilterFactory {

	private GraphingExpressionFilterFactory() {
		// Factory class
	}

	/**
	 * Creates a new ExpressionFilter.
	 * @return expression filter
	 */
	public static ExpressionFilter createFilter() {
		List<ExpressionFilter> filters = Arrays.asList(new AbsExpressionFilter(),
				new InnerProductExpressionFilter(),
				new PowerInnerProductExpressionFilter(),
				new VectorProductExpressionFilter());
		CompositeExpressionFilter compositeExpressionFilter =
				new CompositeExpressionFilter(filters);
		return new DeepExpressionFilter(compositeExpressionFilter);
	}
}
