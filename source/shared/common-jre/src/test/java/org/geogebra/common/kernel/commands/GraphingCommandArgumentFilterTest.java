package org.geogebra.common.kernel.commands;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraphingCommandArgumentFilterTest extends BaseAppTestSetup {

	@BeforeEach
	public void setUp() {
		setApp(AppCommonFactory.create(new AppConfigGraphing()));
	}

	@Test
	public void testParallelLineWithPointAndLineIsFiltered() {
		evaluateGeoElement("A = (1,1)");
		evaluateGeoElement("B = (2,2)");
		evaluateGeoElement("C = (3,2)");
		evaluateGeoElement("f:Line(B,C)");
		assertNull(evaluate("g:Line(A,f)"));
	}

	@Test
	public void testParallelLineWithPointAndFunctionIsFiltered() {
		evaluateGeoElement("A = (1,2)");
		evaluateGeoElement("f(x) = x");
		assertNull(evaluate("g:Line(A,f)"));
	}

	@Test
	public void testLineWithTwoPointsAllowed() {
		evaluateGeoElement("A = (1,2)");
		evaluateGeoElement("B = (3,4)");
		assertNotNull(evaluateGeoElement("g: Line(A, B)"));
	}

	@Test
	public void testLengthOfListAllowed() {
		evaluateGeoElement("L = {(0,0), (1,1), (2,2)}");
		assertNotNull(evaluateGeoElement("Length(L)"));
	}

	@Test
	public void testLengthOfTextAllowed() {
		evaluateGeoElement("text = Text(\"1234\")");
		assertNotNull(evaluateGeoElement("Length(text)"));
	}

	@Test
	public void testLengthOfVectorIsFiltered() {
		evaluateGeoElement("vector = (1,2)");
		assertNull(evaluate("Length(vector)"));
	}

	@Test
	public void testLengthFunctionStartXValueEndXValueIsFiltered() {
		assertNull(evaluate("a = Length(2 x, 0, 1)"));
	}

	@Test
	public void testLengthFunctionStartPointEndPointIsFiltered() {
		assertNull(evaluate("a = Length(2 x, (0,0), (1,1))"));
	}

	@Test
	public void testLengthCurveStartTValueEndTValueIsFiltered() {
		evaluateGeoElement("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
		assertNull(evaluate("Length(curve, 1, 7)"));
	}

	@Test
	public void testLengthCurveStartPointEndPointIsFiltered() {
		evaluateGeoElement("curve = Curve(2 cos(t), 2 sin(t), t, 0, 2π)");
		assertNull(evaluate("Length(curve, (2,0), (0,-2))"));
	}

	@Test
	public void testPolylineWithPointsFiltered() {
		assertNull(evaluate("Polyline((1, 3), (4, 3))"));
	}

	@Test
	public void testPenStrokeWithPointsShouldBeAllowed() {
		assertNotNull(evaluateGeoElement("PenStroke((1, 3), (4, 3))"));
	}

	@Test
	public void testFunction() {
		assertNull(evaluate("Function(x,1,2)"));
		assertNull(evaluate("Function(x+y,x,1,2,y,1,2)"));
		assertNotNull(evaluateGeoElement("Function({1,2,3,4})"));
	}
}
