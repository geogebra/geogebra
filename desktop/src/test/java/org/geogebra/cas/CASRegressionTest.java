package org.geogebra.cas;

import static org.junit.Assume.assumeTrue;

import org.geogebra.desktop.main.AppD;
import org.junit.Test;

public class CASRegressionTest extends BaseCASIntegrationTest {

	@Test
	public void ticket_TRAC_2343() {
		assumeTrue(AppD.WINDOWS);
		// https://jira.geogebra.org/browse/TRAC-2343
		setupCas();
		t("c := Ellipse[(1, 1), (3, 2), (2, 3)]",
				"8 * sqrt(10) * x^(2) - 32 * sqrt(10) * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 12 * x^(2) - 16 * x * y - 24 * x + 24 * y^(2) - 40 * y = 0",
				"8 * x^(2) * sqrt(10) + 12 * x^(2) - 32 * x * sqrt(10) - 16 * x * y - 24 * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 24 * y^(2) - 40 * y = 0");
		t("f(x) := Element[Solve[c, y], 2]",
				"(2 * x + 3 * sqrt(10) - 3 * sqrt(x^(2) * (-2 * sqrt(10) - 6) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) + 5) / (2 * sqrt(10) + 6)",
				"(2 * x + 3 * sqrt(10) + 3 * sqrt(x^(2) * (-2 * sqrt(10) - 6) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) + 5) / (2 * sqrt(10) + 6)",
				"(x * (-4 * sqrt(10) + 6) - sqrt(10) + 3 * sqrt(x^(2) * (-sqrt(10) * 26 - 54) + x * (sqrt(10) * 104 + 216) - sqrt(10) * 38 - 5) - 45) / (-6 * sqrt(10) - 22)");
		t("Solve[f'(x) = 0, x]",
				"{x = (-sqrt(31 * (2 * sqrt(10) - 3)) + 62) / 31}",
				"{x = (-sqrt(2 * sqrt(10) - 3) * sqrt(31) + 62) / 31}",
				"{x = (sqrt(31 * (2 * sqrt(10) - 3)) + 62) / 31}");
		t("g(x) := f'(x)",
				"(x^(2) * (-2 * sqrt(10) - 6) + (x * (3 * sqrt(10) + 9) - 6 * sqrt(10) - 18) * sqrt(x^(2) * (-2 * sqrt(10) - 6) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) / (x^(2) * (-12 * sqrt(10) - 38) + x * (48 * sqrt(10) + 152) - 11 * sqrt(10) - 35)",
				"(x^(2) * (-2 * sqrt(10) - 6) + (x * (3 * sqrt(10) + 9) - 6 * sqrt(10) - 18) * sqrt(x^(2) * (-2 * sqrt(10) - 6) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) / (x^(2) * (-sqrt(10) * 12 - 38) + x * (sqrt(10) * 48 + 152) - sqrt(10) * 11 - 35)",
				"(x^(2) * (-2 * sqrt(10) - 6) + (x * (-3 * sqrt(10) - 9) + 6 * sqrt(10) + 18) * sqrt(x^(2) * (-2 * sqrt(10) - 6) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) / (x^(2) * (-12 * sqrt(10) - 38) + x * (48 * sqrt(10) + 152) - 11 * sqrt(10) - 35)",
				"(x^(2) * (2 * sqrt(10) + 6) + (x * (-3 * sqrt(10) - 9) + 6 * sqrt(10) + 18) * sqrt(x^(2) * (-2 * sqrt(10) - 6) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) + x * (-8 * sqrt(10) - 24) + 2 * sqrt(10) + 5) / (x^(2) * (12 * sqrt(10) + 38) + x * (-48 * sqrt(10) - 152) + 11 * sqrt(10) + 35)",
				"(x^(2) * (sqrt(10) * 30 + 358) + (x * (-sqrt(10) * 39 - 81) + sqrt(10) * 78 + 162) * sqrt(x^(2) * (-sqrt(10) * 26 - 54) + x * (sqrt(10) * 104 + 216) - sqrt(10) * 38 - 5) + x * (-sqrt(10) * 120 - 1432) - sqrt(10) * 104 + 745) / (x^(2) * (sqrt(10) * 448 + 1374) + x * (-sqrt(10) * 1792 - 5496) + sqrt(10) * 433 + 1195)",
				"(x^(2) * (2 * sqrt(10) + 6) + (x * (3 * sqrt(10) + 9) - 6 * sqrt(10) - 18) * sqrt(x^(2) * (-2 * sqrt(10) - 6) + x * (8 * sqrt(10) + 24) - 2 * sqrt(10) - 5) + x * (-8 * sqrt(10) - 24) + 2 * sqrt(10) + 5) / (x^(2) * (12 * sqrt(10) + 38) + x * (-48 * sqrt(10) - 152) + 11 * sqrt(10) + 35)");
		t("Solve[g(x) = 0, x]",
				"{x = (-sqrt(31 * (2 * sqrt(10) - 3)) + 62) / 31}",
				"{x = (-sqrt(2 * sqrt(10) - 3) * sqrt(31) + 62) / 31}",
				"{x = (sqrt(31 * (2 * sqrt(10) - 3)) + 62) / 31}");

	}
}
