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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.gui.view.table.dimensions.LaTeXTextSizeMeasurer;
import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class LaTeXTextSizeMeasurerTests {

	private LaTeXTextSizeMeasurer measurer;

	@BeforeEach
	void setup() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		measurer = new LaTeXTextSizeMeasurer(10);
	}

	@Test
	void testValidFormula() {
		int width = measurer.getWidth("10");
		assertTrue(width > 0);
	}

	/** APPS-4584 */
	@Test
	void testInvalidFormula() {
		int width = measurer.getWidth("&"); // this should not throw
		assertEquals(0, width);
	}
}
