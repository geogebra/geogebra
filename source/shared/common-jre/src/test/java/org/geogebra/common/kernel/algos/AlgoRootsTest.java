package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class AlgoRootsTest extends BaseAppTestSetup {
	@Test
	public void intersectLine() {
		setupApp(SuiteSubApp.G3D);
		GeoElementND element =
				evaluateGeoElement("Intersect(-2 x^(3)+7 x^(2)-2 x-3,4.5 x+y=15.75,-3,0)");
		assertEquals("(-1.5, 22.5)", element.toValueString(StringTemplate.testTemplate));
	}
}
