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

package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class SlopeTest extends BaseUnitTest {

	@Before
	public void setUp() {
		getApp().setGeometryConfig();
	}

	@Test
	public void showInEuclidianView() {
		addAvInput("f = Line((0,0), (1,1))");
		GeoNumeric slope = addAvInput("s = Slope(f)");
		assertThat(slope.showInEuclidianView(), is(true));
	}
}
