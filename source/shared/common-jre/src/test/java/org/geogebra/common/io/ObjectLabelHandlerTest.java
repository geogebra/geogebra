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
 
package org.geogebra.common.io;

import static org.junit.Assert.assertArrayEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class ObjectLabelHandlerTest extends BaseUnitTest {

	@Test
	public void shouldFindObjects() {
		add("Slider[1,4]");
		add("(1,1)+(3,2)");
		add("Midpoint(A,(0,0))");
		assertArrayEquals(new String[]{"a", "A", "B"},
				ObjectLabelHandler.findObjectNames(getApp().getXML()));
	}

}
