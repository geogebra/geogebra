package org.geogebra.common.kernel.cas;

import org.geogebra.suite.BaseSuiteTest;
import org.junit.Test;

public class AlgoSolveTest extends BaseSuiteTest {

	@Test
	public void testSolveResultsAreInDegrees() {
		t("l1=Solve(sin(x)=0.5)", "{x = 30*°, x = 150*°}");
		t("l2=Solve(sin(0.5 x)=0.5)", "{x = 60*°, x = 300*°}");
	}
}
