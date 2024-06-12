package org.geogebra.common.kernel.implicit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.OrderingComparison;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.ScopedMock;

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

	@Test
	public void variableDegreeShouldNotChangeLayer() {
		add("a=1");
		add("c:x^a+y=1");
		add("SetLayer(a,2)");
		assertThat(lookup("c").getLayer(), equalTo(0));
		add("SetValue(a,3)");
		assertThat(lookup("c").getLayer(), equalTo(0));
	}

	@Test
	public void shouldNotUseBigDecimal() {
		add("m=1");
		AtomicInteger counter = new AtomicInteger(0);
		MockedConstruction.MockInitializer<BigDecimal> init = (decimal, context) -> {
			Mockito.when(decimal.multiply(ArgumentMatchers.any())).thenReturn(decimal);
			Mockito.when(decimal.divide(ArgumentMatchers.any(),
					ArgumentMatchers.<RoundingMode>any())).thenReturn(decimal);
			Mockito.when(decimal.divide(ArgumentMatchers.any(),
					ArgumentMatchers.<MathContext>any())).thenReturn(decimal);
			counter.incrementAndGet();
		};
		try (ScopedMock ignore = Mockito.mockConstruction(BigDecimal.class, init)) {
			add("eq1: y^(2) = (sin(x))^(2) + ((sin(pi / 2) / (m^(2) + x^(2)) * (cos(m) + "
					+ "sin(m))^(2))) / tan((x^(2) + sin(pi / (16 + m^(2)))) / sec(m + x))");
		}
		assertThat(counter.get(), OrderingComparison.lessThan(50));
	}
}
