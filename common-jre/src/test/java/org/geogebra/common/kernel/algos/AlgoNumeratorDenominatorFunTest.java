package org.geogebra.common.kernel.algos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class AlgoNumeratorDenominatorFunTest extends BaseUnitTest {

	@Test
	public void testNumeratorConst() {
		t("Numerator(0.125/(1+0 x))", "0.125");
	}
}
