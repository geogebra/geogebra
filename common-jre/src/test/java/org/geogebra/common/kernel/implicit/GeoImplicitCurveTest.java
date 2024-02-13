package org.geogebra.common.kernel.implicit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoImplicitCurveTest extends BaseUnitTest {

	@Test
	public void toValueStringTest() {
		GeoElement implicit = add("sqrt(2)/sqrt(x)=4");
		assertThat(implicit.toValueString(StringTemplate.algebraTemplate),
				is("r(2) / r(x) = 4".replace('r', Unicode.SQUARE_ROOT)));
	}

	@Test
	public void variableDegreeTest() {
		add("U=1");
		add("rho=1");
		add("c:(x^rho+y^rho)^(1/rho)=U");
		assertThat(add("pt=Intersect(c,x=0)"), hasValue("(0, 1)"));
		t("Delete(pt)");
		t("SetValue(rho,3)");
		assertThat(add("Intersect(c,x=0)"), hasValue("(0, 1)"));
	}

	@Test
	public void polynomialShouldShowAsPlainTextInAlgebraView() {
		GeoImplicitCurve poly = add("0=x+y^4");
		assertThat(poly.isLaTeXDrawableGeo(), equalTo(false));
		GeoImplicitCurve nonPoly = add("0=sqrt(x)+y^4");
		assertThat(nonPoly.isLaTeXDrawableGeo(), equalTo(true));
	}
}
