/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.OrderingComparison;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.ScopedMock;

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
		poly.setToImplicit();
		assertThat(poly.isLaTeXDrawableGeo(), equalTo(false));
		GeoImplicitCurve nonPoly = add("0=sqrt(x)+y^4");
		nonPoly.setToImplicit();
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
			Mockito.when(decimal.add(ArgumentMatchers.any())).thenReturn(decimal);
			Mockito.when(decimal.subtract(ArgumentMatchers.any())).thenReturn(decimal);
			Mockito.when(decimal.pow(ArgumentMatchers.anyInt())).thenReturn(decimal);
			counter.incrementAndGet();
		};
		try (ScopedMock ignore = Mockito.mockConstruction(BigDecimal.class, init)) {
			add("eq1: y^(2) = (sin(x))^(2) + ((sin(pi / 2) / (m^(2) + x^(2)) * (cos(m) + "
					+ "sin(m))^(2))) / tan((x^(2) + sin(pi / (16 + m^(2)))) / sec(m + x))");
		}
		assertThat(counter.get(), OrderingComparison.lessThan(100));
	}
}
