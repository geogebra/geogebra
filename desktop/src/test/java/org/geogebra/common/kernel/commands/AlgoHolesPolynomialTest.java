package org.geogebra.common.kernel.commands;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Test;

public class AlgoHolesPolynomialTest extends AlgebraTest {
	private EvalInfo info;

	@Test
	public void testRegressionAppsApps2348() {
		GeoElementND[] slider = ap.processAlgebraCommand("a=10",false);
		GeoFunction func = processComandCreatesFunction("1/(x+a)");
		GeoElementND[] result = ap.processAlgebraCommand("Holes[f]", false);
		assertNotNull(result);
	}

	private GeoFunction processComandCreatesFunction(String command) {
		GeoElementND[] elementNDS = ap.processAlgebraCommand(command, false);
		return (GeoFunction) elementNDS[0];
	}
}
