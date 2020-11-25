package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.junit.Test;

public class AssumeCommandTest extends BaseSymbolicTest {

	@Test
	public void testAssume() {
		t("Assume(a > 0, Integral(exp(-a x), 0, infinity))", "1 / a");
		t("Assume(x>0 && n>0, Solve(log(n^2*(x/n)^lg(x))=log(x^2), x))",
				"{x = 100, x = n}");
		t("Assume(x<2,Simplify(sqrt(x-2sqrt(x-1))))", "-sqrt(x - 1) + 1");
		t("Assume(x>2,Simplify(sqrt(x-2sqrt(x-1))))", "sqrt(x - 1) - 1");
		t("Assume(k>0, Extremum(k*3*x^2/4-2*x/2))",
				"{(2 / (3 * k), (-1) / (3 * k))}");
		t("Assume(k>0, InflectionPoint(0.25 k x^3 - 0.5x^2 + k))",
				"{(2 / (3 * k), (27 * k^(3) - 4) / (27 * k^(2)))}");
	}
}
