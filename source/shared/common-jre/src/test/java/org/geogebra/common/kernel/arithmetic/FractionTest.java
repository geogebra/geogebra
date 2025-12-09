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

package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.OrderingComparison;
import org.geogebra.test.annotation.Issue;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

public class FractionTest extends SymbolicArithmeticTest {

	@Before
	public void setupRounding() {
		getKernel().setPrintDecimals(10);
	}

	@Test
	public void functionWithFractions() {
		add("fracPower(x)=(3/2)^x");
		t("fracPower(2)", "9 / 4");
		t("fracPower(-1)", "2 / 3");
		t("fracPower(-2)", "4 / 9");
	}

	@Test
	public void errorFunctionShouldNotBeFraction() {
		t("erf(5)+1/2", "1.5");
		t("erf(10)+1/2", "3 / 2"); // underflow, indistinguishable from 1+1/2
	}

	@Test
	@Issue("APPS-6484")
	public void multiplesOfPiShouldBeFraction() {
		GeoNumeric frac = add("f:-13 pi/9");
		frac.setSymbolicMode(true, false);
		assertThat(frac, hasValue("-13" + Unicode.PI_STRING + " / 9"));
		add("f:pi + pi/2");
		assertThat(frac, hasValue("3" + Unicode.PI_STRING + " / 2"));
	}

	@Test
	public void scientificNotation() {
		t("5*10^(-2)", "1 / 20");
	}

	@Test
	public void performanceCheck() {
		long time = System.currentTimeMillis();
		add("A1=1/2");
		add("B1=3/2");
		for (int k = 2; k <= 12; k++) {
			add("A" + k + "=A" + (k - 1) + "+ B" + (k - 1) + "-A" + (k - 1));
			add("B" + k + "=B" + (k - 1) + "+ A" + (k - 1) + "-B" + (k - 1));
		}
		GeoElement a12 = lookup("A12");
		((GeoNumeric) a12).setSymbolicMode(true, false);
		// A and B were swapped 11 times
		assertThat(a12, hasValue("3 / 2"));
		assertThat(System.currentTimeMillis() - time, OrderingComparison.lessThan(1000L));
	}

	@Test
	public void testExactFractionDecimals() {
		getKernel().setPrintDecimals(2);
		assertThat(add("1/3"), hasExactFraction(false));
		assertThat(add("1/1000"), hasExactFraction(false));
		assertThat(add("1/2"), hasExactFraction(true));
		assertThat(add("-1/2"), hasExactFraction(true));
		assertThat(add("1/20"), hasExactFraction(true));
		assertThat(add("500/1000"), hasExactFraction(true));
		assertThat(add("501/1000"), hasExactFraction(false));
		assertThat(add("51/1000"), hasExactFraction(false));
	}

	@Test
	public void testExactFractionPi() {
		assertThat(add("(3 pi)/2"), hasExactFraction(false));
		assertThat(add("pi/2"), hasExactFraction(false));
	}

	@Test
	public void testExactFractionSimplification() {
		t("15.3/2.55", "6");
		t("19.69", "1969 / 100");
	}

	@Test
	public void testExactFractionSignificantDigits() {
		getKernel().setPrintFigures(2);
		assertThat(add("1/3"), hasExactFraction(false));
		assertThat(add("1/1000"), hasExactFraction(true));
		assertThat(add("1/2"), hasExactFraction(true));
		assertThat(add("1/20"), hasExactFraction(true));
		assertThat(add("500/1000"), hasExactFraction(true));
		assertThat(add("501/1000"), hasExactFraction(false));
		assertThat(add("51/1000"), hasExactFraction(true));
	}

	private Matcher<GeoElement> hasExactFraction(boolean flag) {
		return hasProperty("exact fraction",
				geo -> Fractions.isExactFraction(geo, getKernel()), flag);
	}
}
