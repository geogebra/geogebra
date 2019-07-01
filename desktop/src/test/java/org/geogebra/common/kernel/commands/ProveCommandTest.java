package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProveCommandTest {
	private static AppDNoGui app;
	private static AlgebraProcessor proc;

	/**
	 * Creates the app
	 */
	@BeforeClass
	public static void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
		proc = app.getKernel().getAlgebraProcessor();
	}

	private static void t(String s, String expected) {
		CommandsTest.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				proc, StringTemplate.defaultTemplate);
	}

	@Test
	public void cmdProveDetails() {
		t("P=(1,1)", "(1, 1)");
		t("ProveDetails[ (1,1)==(1,1) ]", "{true}");
		t("ProveDetails[ (1,1)==P ]", "{}");
	}

	@Test
	public void cmdProve() {
		t("P=(1,1)", "(1, 1)");
		t("Prove[ (1,1)==(1,1) ]", "true");
		t("Prove[ (1,1)==P ]", "false");
	}

	@Test
	public void cmdLocusEquation() {
		t("c=Circle((0,0), 2)", TestStringUtil.unicode("x^2 + y^2 = 4"));
		t("A=Point(c)", "(2, 0)");
		t("O=(0, 0)", "(0, 0)");
		t("B = Midpoint(A, O)", "(1, 0)");
		t("LocusEquation[ B, A ]", TestStringUtil.unicode("x^2 + y^2 = 1"));
		t("loc=Locus(B, A)", "Locus(B, A)");
		
		t("LocusEquation[ loc ]", TestStringUtil.unicode("x^2 + y^2 = 1"));
		t("P=(1,0)", "(1, 0)");
		t("seg1 = Segment(P, (0,0))", "1");
		t("LocusEquation[ seg1==1, P ]",
				TestStringUtil.unicode("x^2 + y^2 = 1"));
	}
}
