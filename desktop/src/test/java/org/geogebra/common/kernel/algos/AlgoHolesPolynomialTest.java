package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import static org.junit.Assert.assertThat;


import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Test;

public class AlgoHolesPolynomialTest extends BaseSymbolicTest {
	@Test
	public void testRegressionAppsApps2348() {
		add("a=1");
		GeoElementND holes = add("Holes[1/(x+a)]");
		assertThat(holes, isNotNull());
	}
}
