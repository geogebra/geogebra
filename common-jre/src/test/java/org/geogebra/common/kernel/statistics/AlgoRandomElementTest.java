package org.geogebra.common.kernel.statistics;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.junit.Assert;
import org.junit.Test;

public class AlgoRandomElementTest extends BaseUnitTest {

	@Test
	public void testNumberResultSymbolicMode() {
		GeoElement symbolicNumber = add("RandomElement({1/2,1/3,1/4})");
		assertNumberSymbolicMode(symbolicNumber, true);

		GeoElement nonSymbolicNumber = add("RandomElement({0.5,0.3})");
		assertNumberSymbolicMode(nonSymbolicNumber, false);
	}

	private void assertNumberSymbolicMode(GeoElement element, boolean symbolic) {
		Assert.assertTrue(element instanceof HasSymbolicMode);
		HasSymbolicMode elementWithSymbolicMode = (HasSymbolicMode) element;
		Assert.assertEquals(symbolic, elementWithSymbolicMode.isSymbolicMode());
	}

	@Test
	public void testFunctionResultSymbolicMode() {
		GeoElement symbolicFunction = add("f(x)=RandomElement({1/2}) x^2");
		assertOutput(symbolicFunction, "1 / 2x²");

		GeoElement nonSymbolicFunction = add("f(x)=RandomElement({0.2}) x^3");
		assertOutput(nonSymbolicFunction, "0.2x³");
	}

	private void assertOutput(GeoElement element, String expectedOutput) {
		String actualOutput = element.toOutputValueString(StringTemplate.defaultTemplate);
		Assert.assertEquals(expectedOutput, actualOutput);
	}
}
