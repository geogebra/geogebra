package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.jupiter.api.Test;

public class AlgoRootsTest {

	private final AppCommon app = AppCommonFactory.create3D();

	@Test
	public void intersectLine() {
		GeoElementND element = add("Intersect(-2 x^(3)+7 x^(2)-2 x-3,4.5 x+y=15.75,-3,0)");
		assertEquals("(-1.5, 22.5)", element.toValueString(StringTemplate.testTemplate));
	}

	private GeoElementND add(String command) {
		return app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(command, false)[0];
	}
}
