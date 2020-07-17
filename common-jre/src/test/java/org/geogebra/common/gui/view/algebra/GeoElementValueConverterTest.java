package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Assert;
import org.junit.Test;

public class GeoElementValueConverterTest extends BaseUnitTest {

	private GeoElementValueConverter converter = new GeoElementValueConverter();

	@Test
	public void testConvertsSimpleNumber() {
		String[] numbers = {"5", "-2", "3.5", "1000"};
		for (String number : numbers) {
			assertConverts(number, number);
		}
	}

	@Test
	public void testAddsBracketsForComplexExpressions() {
		assertConverts("y=3x", "y = 3x");
		assertConverts("x^2", "x²");
	}

	@Test
	public void testDoesNotIncludeLabel() {
		assertConverts("a = 1", "1");
		assertConverts("f(x) = x+2", "x + 2");
		assertConverts("d: y = 5x", "y = 5x");
	}

	@Test
	public void testConvertsCommandsIntoValue() {
		assertConverts("Line((1,2), (3,4))", "-x + y = 1");
		assertConverts("Circle((0,0), 2)", "x² + y² = 4");
	}

	private void assertConverts(String input, String expected) {
		GeoElement element = addAvInput(input);
		Assert.assertEquals(expected, converter.convert(element));
	}
}
