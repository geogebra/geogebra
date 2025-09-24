package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoNameTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	@Issue("APPS-6919")
	public void testNameForElement() {
		evaluate("a=1");
		evaluate("P=Element(Sequence(5),1)");
		evaluate("Q=Element({a},1)");
		GeoText nameListOfObjects = evaluateGeoElement("Name(Q)");
		assertEquals("a", nameListOfObjects.getTextString());
		GeoText nameSequence = evaluateGeoElement("Name(P)");
		assertEquals("P", nameSequence.getTextString());
	}
}
