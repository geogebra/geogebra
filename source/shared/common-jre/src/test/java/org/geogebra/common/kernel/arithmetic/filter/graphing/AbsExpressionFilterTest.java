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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.junit.jupiter.api.Test;

class AbsExpressionFilterTest extends BaseUnitTest {

	final AbsExpressionFilter filter = new AbsExpressionFilter();

	@Test
	void testPointsAreRestricted() {
		assertFalse(isAllowed("abs((1,2))"));
		assertFalse(isAllowed("abs((1,2) + (3,4))"));
		assertFalse(isAllowed("abs(Point({1,2}))"));
	}

	@Test
	void testVectorsAreRestricted() {
		assertFalse(isAllowed("abs({{1},{2}})"));
		assertFalse(isAllowed("abs({{1},{2}} + {{3},{4}})"));
		assertFalse(isAllowed("abs(Vector((1,2)))"));
	}

	@Test
	void testNumbersAreAllowed() {
		assertTrue(isAllowed("abs(-4)"));
		assertTrue(isAllowed("abs(1 + 2 * 8)"));
		assertTrue(isAllowed("abs(Length({{1},{2}}))"));
	}

	@Test
	void testComplexNumbersAreRestricted() {
		assertFalse(isAllowed("abs(1 + i)"));
	}

	private boolean isAllowed(String input) {
		ValidExpression expression = parse(input);
		return filter.isAllowed(expression);
	}

	private ValidExpression parse(String input) {
		try {
			return getKernel().getAlgebraProcessor().getValidExpressionNoExceptionHandling(input);
		} catch (Exception e) {
			throw new AssertionError("Exception thrown ", e);
		}
	}
}
