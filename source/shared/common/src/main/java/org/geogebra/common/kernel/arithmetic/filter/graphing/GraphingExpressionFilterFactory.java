package org.geogebra.common.kernel.arithmetic.filter.graphing;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.filter.CompositeExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.InspectingExpressionFilter;

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
		return new InspectingExpressionFilter(compositeExpressionFilter);
	}
}
