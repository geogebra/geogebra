package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoImplicitCurve3DTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	@Issue("APPS-5381")
	public void shouldReplaceLocalVariables() {
		GeoList seq = add("l1=Sequence(Translate[0=x*y+cos(x+2*k)-1,Vector((0,0,k))],k,1,2)");
		assertThat(seq, hasValue("{(0 - (x y + cos(x + 2 * 1) - 1) = 0,z = 1),"
				+ " (0 - (x y + cos(x + 2 * 2) - 1) = 0,z = 2)}"));
	}

	@Test
	@Issue("APPS-5381")
	public void shouldHaveCorrectVariablesReplaceLocalVariables() {
		GeoImplicitCurve3D base = add("IntersectPath(x^3+y^4,z=1)");
		assertThat(base, hasValue(unicode("(x^3 + y^4 = 1,z = 1)")));
		GeoImplicitCurve3D xyPlane = add("IntersectPath(x^3+y^4,x+y+0z=1)");
		assertThat(xyPlane, hasValue(unicode("(x^4 - 3x^3 + 6x^2 - 4x - z = -1,x + y = 1)")));
		GeoImplicitCurve3D xPlane = add("IntersectPath(x^3+y^4,x+0z=1)");
		assertThat(xPlane, hasValue(unicode("(y^4 - z = -1,x = 1)")));
	}

	@Test
	@Issue("APPS-5381")
	public void polynomialShouldShowAsPlainTextInAlgebraView() {
		GeoImplicitCurve3D poly = add("Translate(0=x+y^4, (0,0,1))");
		poly.setToImplicit();
		assertThat(poly.isLaTeXDrawableGeo(), equalTo(false));
		GeoImplicitCurve3D nonPoly = add("Translate(0=sin(x)+y^4, (0,0,1))");
		nonPoly.setToImplicit();
		assertThat(nonPoly.isLaTeXDrawableGeo(), equalTo(true));
	}

}
