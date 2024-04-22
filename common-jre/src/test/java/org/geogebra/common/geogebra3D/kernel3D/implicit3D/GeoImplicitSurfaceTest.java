package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Test;

public class GeoImplicitSurfaceTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		AppCommon app = AppCommonFactory.create3D();
		app.setPrerelease();
		return app;
	}

	@Test
	public void implicitSurface() {
		GeoImplicitSurface surface = add("x^3+y^3+z^3=1");
		assertThat(surface, isDefined());
	}

}
