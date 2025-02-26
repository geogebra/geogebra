package org.geogebra.common.exam.restrictions.realschule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.junit.Before;
import org.junit.Test;

public class RealschuleAlgebraOutputFilterTests extends BaseExamTests {

	@Before
	public void setup() {
		setInitialApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testAlgebraOutputRestrictions() {
		AlgebraOutputFilter outputFilter = new RealschuleAlgebraOutputFilter(null);

		assertFalse(outputFilter.isAllowed(evaluateGeoElement("Line((0, 0), (1, 2))")));
		assertFalse(outputFilter.isAllowed(evaluateGeoElement("Ray((0, 0), (1, 2))")));
		assertFalse(outputFilter.isAllowed(evaluateGeoElement("Circle((0, 0), 1)")));

		assertTrue(outputFilter.isAllowed(evaluateGeoElement("FitLine((1,1), (2,3))")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement(
				"FitImplicit((1...10,(1/(1...10))),3)")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement(
				"f(x)=FitPoly({(-2,1),(-1,0),(0,1),(1,0)},3)")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("FitExp((1,1),(2,4))")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("FitGrowth((1,2),(3,4))")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("FitLogistic((1,2),(3,4),(5,6))")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("FitPow((1,2),(3,4))")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("FitSin((3,3),(4,4))")));

		assertTrue(outputFilter.isAllowed(evaluateGeoElement("x = y")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("x^2 + y^2 = 4")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("x^3 + y = 0")));
		assertTrue(outputFilter.isAllowed(evaluateGeoElement("x")));
	}

}