package org.geogebra.common.kernel.cas;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.suite.BaseSuiteTest;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoSolveTest extends BaseSuiteTest {

	@Test
	public void testSolveResultsAreInDegrees() {
		t("l1=Solve(sin(x)=0.5)", unicode("{x = 30*deg, x = 150*deg}"));
		t("l4=Solve(sin(30deg)=sin(x))", unicode("{x = 30*deg, x = 150*deg}"));
		t("l1=Solve(sin(x+5deg)=0.5)", unicode("{x = 25*deg, x = 145*deg}"));
		t("l2=Solve(sin(0.5 x)=0.5)", unicode("{x = 60*deg, x = 300*deg}"));
		t("l3=NSolve(sin(abs(x))=0.5)",
				unicode("{x = -150*deg, x = -30*deg, x = 30*deg, x = 150*deg}"));
		t("l5=Solve(sin(360deg/x)=0.5)", "{x = 12 / 1, x = 12 / 5}");
		t("l3=Solve(sin(30deg)=1/x)", "{x = 2}");
		t("l3=Solve(sin(30deg)=x)", "{x = 1 / 2}");
		t("l3=Solve(sin(30deg)=sin(x deg))", "{x = 30, x = 150}");
		t("Solve(sin(pi*x/4)=sqrt(2)/2)", "{x = 1, x = 3}");
	}

	@Test
	@Issue("APPS-6278")
	public void shouldNotSendTooManyZerosToCas() {
		add("f(x)=5.4 sin(((2 π)/(365)) (x-75))+12");
		GeoElementND solve = add("NSolve(f(x)=17.4)");
		assertEquals("{x = 166.25, x = 166.25}",
				solve.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-6615")
	public void shouldSendEnoughBracketsToCas() {
		GeoNumeric number = add("a=0.3");
		number.setSymbolicMode(true, false);
		assertEquals(add("NSolve(a^2=x)").toValueString(StringTemplate.defaultTemplate),
				"{x = 0.09}");
		add("a=1-0.3");
		assertEquals(add("NSolve(a^2=x)").toValueString(StringTemplate.defaultTemplate),
				"{x = 0.49}");
	}
}
