package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.PreviewFeature;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeoImplicitSurfaceTest extends BaseUnitTest {

	@BeforeClass
	public static void enablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
	}

	@AfterClass
	public static void disablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void implicitSurface() {
		GeoImplicitSurface surface = add("x^3+y^3+z^3=1");
		assertThat(surface, isDefined());
	}

}
