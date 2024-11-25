package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoConic;
import org.junit.Test;

public class AlgoExtremumMultiTest extends BaseUnitTest {

	@Test
	public void extremumShouldNotOverwriteVariables() {
		GeoConic conic = add("f:y=4-x^2");
		Function eval = conic.getFunction();
		assertEquals(0, eval.value(2), Kernel.STANDARD_PRECISION);
		add("Extremum(f)");
		assertEquals(0, eval.value(2), Kernel.STANDARD_PRECISION);

	}
}
