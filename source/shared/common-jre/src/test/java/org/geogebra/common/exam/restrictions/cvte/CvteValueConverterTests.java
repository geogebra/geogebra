package org.geogebra.common.exam.restrictions.cvte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;
import org.junit.Before;
import org.junit.Test;

public class CvteValueConverterTests extends BaseExamTests {

	@Before
	public void setup() {
		setInitialApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testValueRestrictions() {
		ToStringConverter<GeoElement> converter = new CvteValueConverter(null);

		// For Lines, Rays, Conics, Implicit Equations and Functions created with command or tool:
		// When using the ANS button the "Definition" is inserted into the AV inputBar.
		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		assertEquals("Line((0, 0), (1, 2))", converter.convert(line));

		GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
		assertEquals("Ray((0, 0), (1, 2))", converter.convert(ray));

		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		assertEquals("Circle((0, 0), 1)", converter.convert(circle));

		GeoElement implicitCurve = evaluateGeoElement("FitImplicit((1…10, 1 / (1…10)), 3)");
		assertEquals("FitImplicit((1…10, 1 / (1…10)), 3)", converter.convert(implicitCurve));

		GeoElement function = evaluateGeoElement("FitPoly({(-2, 1), (-1, 0), (0, 1), (1, 0)}, 3)");
		assertEquals("FitPoly({(-2, 1), (-1, 0), (0, 1), (1, 0)}, 3)", converter.convert(function));

		// negative example
		GeoElement implicitCircle = evaluateGeoElement("xx+yy=1");
		assertNull(converter.convert(implicitCircle));
	}
}