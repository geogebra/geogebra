/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlgoStadiumTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testXml() {
		AlgoStadium algo = createAlgo();
		assertEquals("<command name=\"Stadium\">\n"
				+ "\t<input a0=\"(-4, 1)\" a1=\"(4, 1)\" a2=\"4\"/>\n"
				+ "\t<output a0=\"\"/>\n"
				+ "</command>\n", algo.getXML());
	}

	private AlgoStadium createAlgo() {
		Construction cons = getKernel().getConstruction();
		GeoPoint p = new GeoPoint(cons, -4, 1, 1);
		GeoPoint q = new GeoPoint(cons, 4, 1, 1);
		GeoNumeric height = new GeoNumeric(cons, 4);
		return new AlgoStadium(cons, p, q, height);
	}
}
