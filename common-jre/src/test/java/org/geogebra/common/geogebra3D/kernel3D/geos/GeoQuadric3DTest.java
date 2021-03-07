package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.AppCommon3D;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class GeoQuadric3DTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return new AppCommon3D(new LocalizationCommon(3), new AwtFactoryCommon());
	}

	@Test
	public void eigenvectorsShouldUpdate() {
		add("d=.5");
		GeoQuadric3D quad = add("quad:1=x^(2) + (y^(2) + z^(2)) / (1 - d^(2))");
		MatcherAssert.assertThat(quad.getEigenvec3D(0), hasCoords(1, 0, 0, 0));
		MatcherAssert.assertThat(quad.getEigenvec3D(1), hasCoords(-0.0, 1, 0, 0));
		MatcherAssert.assertThat(quad.getEigenvec3D(2), hasCoords(0, -0.0, 1, 0));
		add("SetValue(d,.02)");
		add("SetValue(d,.5)");
		MatcherAssert.assertThat(quad.getEigenvec3D(0), hasCoords(1, 0, 0, 0));
		MatcherAssert.assertThat(quad.getEigenvec3D(1), hasCoords(-0.0, 1, 0, 0));
		MatcherAssert.assertThat(quad.getEigenvec3D(2), hasCoords(0, -0.0, 1, 0));
	}

	private Matcher<Coords> hasCoords(final double... coords) {
		return new TypeSafeMatcher<Coords>() {
			@Override
			protected boolean matchesSafely(Coords item) {
				return Arrays.equals(coords, item.get());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("coordinates " + Arrays.toString(coords));
			}
		};
	}
}
