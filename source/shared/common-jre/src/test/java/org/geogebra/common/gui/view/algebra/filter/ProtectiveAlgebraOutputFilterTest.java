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

package org.geogebra.common.gui.view.algebra.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Before;
import org.junit.Test;

public class ProtectiveAlgebraOutputFilterTest extends BaseUnitTest {

	private ProtectiveAlgebraOutputFilter filter = new ProtectiveAlgebraOutputFilter();

	@Before
	public void setUp() {
		getApp().setGraphingConfig();
	}

	@Test
	public void isAllowed() {
		GeoElement fitLine = addAvInput("FitLine((0,0),(1,1),(2,2))");
		assertThat(filter.isAllowed(fitLine), is(true));

		GeoElement line = addAvInput("Line((0,0),(1,1))");
		assertThat(filter.isAllowed(line), is(true));

		GeoElement ray = addAvInput("Ray((0,0),(1,1))");
		assertThat(filter.isAllowed(ray), is(false));
	}
}