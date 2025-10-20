package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoPolyhedronTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	public void pointOnPolyhedron() {
		evaluate("a=Cube(O,(0,1,0))");
		GeoNumeric pointX = evaluateGeoElement("x(Point(a,0.2))");
		Assertions.assertEquals(-0.4, pointX.getValue(), Kernel.STANDARD_PRECISION);
	}
}
