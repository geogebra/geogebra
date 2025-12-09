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

package org.geogebra.common.gui.view.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.gui.view.table.dimensions.LaTeXTextSizeMeasurer;
import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class LaTeXTextSizeMeasurerTests {

	private LaTeXTextSizeMeasurer measurer;

	@Before
	public void setup() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		measurer = new LaTeXTextSizeMeasurer(10);
	}

	@Test
	public void testValidFormula() {
		int width = measurer.getWidth("10");
		assertTrue(width > 0);
	}

	/** APPS-4584 */
	@Test
	public void testInvalidFormula() {
		int width = measurer.getWidth("&"); // this should not throw
		assertEquals(0, width);
	}
}
