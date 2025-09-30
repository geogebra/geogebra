package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoConicPartConicPointsTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	public void arcParamsForSemicircle() {
		evaluate("s=Semicircle((-5,0), (5, 0))");
		evaluate("a=Arc(s, (-3,4), (4,3))");
		assertEquals("(-3, 4)", evaluateGeoElement("Point(a,0)").toValueString(
				StringTemplate.editTemplate));
		assertEquals("(4, 3)", evaluateGeoElement("Point(a,1)").toValueString(
				StringTemplate.editTemplate));
	}

	@Test
	public void arcParamsForSemicircle3D() {
		evaluate("s=Semicircle((0, -5,0), (0, 5, 0), x=0z)");
		evaluate("a=Arc(s, (0, -3, 4), (0, 4, 3))");
		assertEquals("(0, -3, 4)", evaluateGeoElement("Point(a,0)").toValueString(
				StringTemplate.editTemplate));
		assertEquals("(0, 4, 3)", evaluateGeoElement("Point(a,1)").toValueString(
				StringTemplate.editTemplate));
	}
}
