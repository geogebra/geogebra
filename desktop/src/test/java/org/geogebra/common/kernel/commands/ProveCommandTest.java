package org.geogebra.common.kernel.commands;

import static org.geogebra.test.TestStringUtil.unicode;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Before;
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

	private static void t(String s, String... expected) {
		CommandsTestCommon.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				proc, StringTemplate.defaultTemplate);
	}

	@Before
	public void clean() {
		app.getKernel().clearConstruction(true);
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
		t("Prove[ (1,1)==P ]", "?");
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

	@Test
	public void cmdEnvelope() {
		t("circ: x^2+y^2=1", unicode("x^2 + y^2 = 1"));
		t("P=Point[circ]", "(1, 0)");
		t("tgt=Tangent[P,circ]", "x = 1", "?");
		t("Envelope[ tgt, P ]", "?");
	}
}
