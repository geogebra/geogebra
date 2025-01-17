package org.geogebra.common.kernel.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.junit.Test;

public class AlgoRandomElementTest extends BaseUnitTest {

	@Test
	public void testNumberResultSymbolicMode() {
		GeoElement symbolicNumber = addAvInput("RandomElement({1/2,1/3,1/4})");
		assertNumberSymbolicMode(symbolicNumber, true);
	}

	private void assertNumberSymbolicMode(GeoElement element, boolean symbolic) {
		assertTrue(element instanceof HasSymbolicMode);
		HasSymbolicMode elementWithSymbolicMode = (HasSymbolicMode) element;
		assertEquals(symbolic, elementWithSymbolicMode.isSymbolicMode());
	}

	@Test
	public void testFunctionResultSymbolicMode() {
		GeoElement symbolicFunction = addAvInput("f(x)=RandomElement({1/2}) x^2");
		assertOutput(symbolicFunction, "1 / 2x²");

		GeoElement nonSymbolicFunction = addAvInput("f(x)=RandomElement({0.2}) x^3");
		assertOutput(nonSymbolicFunction, "0.2x³");
	}

	private void assertOutput(GeoElement element, String expectedOutput) {
		String actualOutput = element.toOutputValueString(StringTemplate.defaultTemplate);
		assertEquals(expectedOutput, actualOutput);
	}
}
