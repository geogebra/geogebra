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

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.junit.Test;

public class CompositeExpressionFilterTest extends BaseUnitTest {

	private final ValidExpression testExpression = new MyDouble(getKernel());
	private final ExpressionFilter allowsFilter = mockFilter(true);
	private final ExpressionFilter preventFilter = mockFilter(false);

	@Test
	public void testCompositeFilterPrevents() {
		CompositeExpressionFilter compositeExpressionFilter = new CompositeExpressionFilter(
				List.of(allowsFilter, preventFilter, allowsFilter));
		boolean result = compositeExpressionFilter.isAllowed(testExpression);
		assertFalse(result);
	}

	@Test
	public void testCompositeFilterAllows() {
		CompositeExpressionFilter compositeExpressionFilter = new CompositeExpressionFilter(
				List.of(allowsFilter, allowsFilter));
		boolean result = compositeExpressionFilter.isAllowed(testExpression);
		assertTrue(result);
	}

	private ExpressionFilter mockFilter(boolean allows) {
		ExpressionFilter filter = mock(ExpressionFilter.class);
		when(filter.isAllowed(any())).thenReturn(allows);
		return filter;
	}
}
