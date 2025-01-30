package org.geogebra.common.exam.restrictions.realschule;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.gui.view.algebra.GeoElementValueConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;
import org.junit.Before;
import org.junit.Test;

public class RealschuleValueConverterTests extends BaseExamTests {

	@Before
	public void setup() {
		setInitialApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testValueRestrictions() {
		ToStringConverter<GeoElement> converter = new RealschuleValueConverter(
				new GeoElementValueConverter());

		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		assertEquals("Line((0, 0), (1, 2))", converter.convert(line));

		GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
		assertEquals("Ray((0, 0), (1, 2))", converter.convert(ray));

		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		assertEquals("Circle((0, 0), 1)", converter.convert(circle));

		GeoElement fitLine = evaluateGeoElement("FitLine((1,1), (2,3))");
		assertEquals("y = 2x - 1", converter.convert(fitLine));

		GeoElement fitExp = evaluateGeoElement("FitExp((1,1),(2,4))");
		assertEquals("0.25ℯ^(1.39x)", converter.convert(fitExp));

		GeoElement fitGrowth = evaluateGeoElement("FitGrowth((1,2),(3,4))");
		assertEquals("1.41 * 1.41^x", converter.convert(fitGrowth));

		GeoElement fitLogistics = evaluateGeoElement("FitLogistic((1,2),(3,4),(5,6))");
		assertEquals("8 / (1 + 5.2ℯ^(-0.55x))", converter.convert(fitLogistics));

		GeoElement fitPow = evaluateGeoElement("FitPow((1,2),(3,4))");
		assertEquals("2x^0.63", converter.convert(fitPow));

		GeoElement fitSin = evaluateGeoElement("FitSin((3,3),(4,4))");
		assertEquals("3.5 + 0.5sin(3.14x - 11)", converter.convert(fitSin));

		GeoElement implicitCurve = evaluateGeoElement("FitImplicit((1…10, 1 / (1…10)), 3)");
		assertEquals("-0.06x² y - 0.7x y + 0.06x = -0.7", converter.convert(implicitCurve));

		GeoElement function = evaluateGeoElement("FitPoly({(-2, 1), (-1, 0), (0, 1), (1, 0)}, 3)");
		assertEquals("-0.67x³ - x² + 0.67x + 1", converter.convert(function));

		GeoElement implicitCircle = evaluateGeoElement("xx+yy=1");
		assertEquals("x² + y² = 1", converter.convert(implicitCircle));
	}
}