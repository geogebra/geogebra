package org.geogebra.common.geogebra3D.kernel3D.geos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.test.annotation.Issue;
import org.hamcrest.Matcher;
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
		assertThat(add("(x-z)(x+z)=0"), hasType("Intersecting Planes"));
		assertThat(add("(x-z)(x-z-1)=0"), hasType("Parallel Planes"));
		assertThat(add("(x-z)(x+z)=1"), hasType("Hyperbolic Cylinder"));
		assertThat(add("(x^2-z)=0"), hasType("Parabolic Cylinder"));
		assertThat(add("x^2+y^2-z^2=1"), hasType("Hyperboloid of one sheet"));
		assertThat(add("x^2+y^2-z^2=-1"), hasType("Hyperboloid of two sheets"));
		assertThat(add("x^2+y^2+z^2=-1"), hasType("Empty Set"));
		assertThat(add("x^2+y^2+z^2=1"), hasType("Sphere"));
		assertThat(add("x^2+y^2+z^2=0"), hasType("Point"));
		assertThat(add("x^2+z^2=0"), hasType("Line"));
	}

	@Test
	@Issue("APPS-6570")
	public void shouldLoadAsImplicitFromFile() {
		add("a=1");
		getApp().getGgbApi().evalXML(
				"<expression label=\"b\" exp=\"a*x^2=z^2\" type=\"quadric\" />"
				+ "<element type=\"quadric\" label=\"b\">"
				+ "<show object=\"true\" label=\"false\" ev=\"7\"/>"
				+ "</element>");
		assertEquals(QuadraticEquationRepresentable.Form.IMPLICIT,
				((GeoQuadric3D) lookup("b")).getEquationForm());
		assertThat(add("FormulaText(b,true,true)"),
				hasValue("b\\mathpunct{:}\\,x² - z²\\, = \\,0"));
	}

	@Test
	public void assignmentInLaTeXShouldHaveOnlyOneSpace() {
		assertEquals("f\\mathpunct{:}\\,y\\, = \\,x^{2} + z",
				add("y=x^2+z").toString(StringTemplate.latexTemplate));
		assertEquals("g\\mathpunct{:}\\,y\\, = \\,x^{2} + z",
				add("y=x^2+z").getLaTeXAlgebraDescription(false, StringTemplate.latexTemplate));
	}

	private Matcher<GeoElement> hasType(String type) {
		return hasProperty("type", GeoElement::translatedTypeString, type);
	}

	private Matcher<Coords> hasCoords(final double... coords) {
		return hasProperty("coordinates", c -> Arrays.toString(c.get()), Arrays.toString(coords));
	}
}
