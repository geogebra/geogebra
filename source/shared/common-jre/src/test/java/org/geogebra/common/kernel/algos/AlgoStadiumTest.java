package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.geogebra.common.BaseAppTest;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.jupiter.api.Test;

public class AlgoStadiumTest extends BaseAppTest {

	private Construction cons;

	@Test
	void testXml() {
		AlgoStadium algo = createAlgo();
		assertEquals("<command name=\"Stadium\">\n"
				+ "\t<input a0=\"(-4, 1)\" a1=\"(4, 1)\" a2=\"4\"/>\n"
				+ "\t<output a0=\"\"/>\n"
				+ "</command>\n", algo.getXML());
	}

	private AlgoStadium createAlgo() {
		cons = getKernel().getConstruction();
		GeoPoint p = new GeoPoint(cons, -4, 1, 1);
		GeoPoint q = new GeoPoint(cons, 4, 1, 1);
		GeoNumeric height = new GeoNumeric(cons, 4);
		return new AlgoStadium(cons, p, q, height);
	}
}
