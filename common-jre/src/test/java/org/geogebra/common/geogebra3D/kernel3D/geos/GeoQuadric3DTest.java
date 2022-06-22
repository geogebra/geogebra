package org.geogebra.common.geogebra3D.kernel3D.geos;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class GeoQuadric3DTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void eigenvectorsShouldUpdate() {
		add("d=.5");
		GeoQuadric3D quad = add("quad:1=x^(2) + (y^(2) + z^(2)) / (1 - d^(2))");
		assertThat(quad.getEigenvec3D(0), hasCoords(1, 0, 0, 0));
		assertThat(quad.getEigenvec3D(1), hasCoords(-0.0, 1, 0, 0));
		assertThat(quad.getEigenvec3D(2), hasCoords(0, -0.0, 1, 0));
		add("SetValue(d,.02)");
		add("SetValue(d,.5)");
		assertThat(quad.getEigenvec3D(0), hasCoords(1, 0, 0, 0));
		assertThat(quad.getEigenvec3D(1), hasCoords(-0.0, 1, 0, 0));
		assertThat(quad.getEigenvec3D(2), hasCoords(0, -0.0, 1, 0));
	}

	@Test
	public void testObjectTypes() {
		assertThat(add("(x-z)(x+z)=0"), hasType("Intersecting planes"));
		assertThat(add("(x-z)(x-z-1)=0"), hasType("Parallel planes"));
		assertThat(add("(x-z)(x+z)=1"), hasType("Hyperbolic Cylinder"));
		assertThat(add("(x^2-z)=0"), hasType("Parabolic cylinder"));
		assertThat(add("x^2+y^2-z^2=1"), hasType("Hyperboloid of one sheet"));
		assertThat(add("x^2+y^2-z^2=-1"), hasType("Hyperboloid of two sheets"));
		assertThat(add("x^2+y^2+z^2=-1"), hasType("Empty set"));
		assertThat(add("x^2+y^2+z^2=1"), hasType("Sphere"));
		assertThat(add("x^2+y^2+z^2=0"), hasType("Point"));
		assertThat(add("x^2+z^2=0"), hasType("Line"));
	}

	private Matcher<GeoElement> hasType(String type) {
		return new TypeSafeMatcher<GeoElement>() {
			@Override
			protected boolean matchesSafely(GeoElement item) {
				return item.translatedTypeString().equals(type);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(type);
			}

			@Override
			public void describeMismatchSafely(GeoElement item, Description mismatchDescription) {
				super.describeMismatch(item.translatedTypeString() + " : " + item,
						mismatchDescription);
			}
		};
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
