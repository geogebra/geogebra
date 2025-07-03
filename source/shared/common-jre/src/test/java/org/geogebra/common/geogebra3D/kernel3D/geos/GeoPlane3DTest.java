package org.geogebra.common.geogebra3D.kernel3D.geos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoPlane3DTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setApp(AppCommonFactory.create3D());
	}

	@Test
	@Issue("APPS-6716")
	public void equationVectorShouldNotChange() {
		GeoPlane3D plane = evaluateGeoElement("Plane((0,0,0),(-1,0,5),(3,2,-1))");
		String expectedCoords = new Coords(-10.0, 14.0, -2.0, -0.0).toString();
		assertEquals(expectedCoords,
				plane.getCoordSys().getEquationVector().toString());
		assertEquals("-5x + 7y - z=0", plane.toValueString(StringTemplate.editorTemplate));
		assertEquals(expectedCoords, plane.getCoordSys().getEquationVector().toString());
	}
}
