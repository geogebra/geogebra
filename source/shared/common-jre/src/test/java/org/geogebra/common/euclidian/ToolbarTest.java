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

package org.geogebra.common.euclidian;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.junit.Test;

public class ToolbarTest {
	@Test
	public void testToolbar() {
		String def = "0 39 73 62 | 1 501 67 , 5 19 , 72 75 76 | 2 15 45 , 18 65 , 7 37"
				+ " | 4 3 8 9 , 13 44 , 58 , 47 | 16 51 64 , 70 | 10 34 53 11 , 24  20 22 , 21 23"
				+ " | 55 56 57 , 12 | 36 46 , 38 49  50 , 71  14  68 | 30 29 54 32 31 33"
				+ " | 25 17 26 60 52 61 | 40 41 42 , 27 28 35 , 6";
		assertTrue(ToolBar.isDefaultToolbar(def));
		assertTrue(ToolBar.isDefaultToolbar(ToolBar.getAllToolsNoMacros(
				false, false, false)));
		assertTrue(ToolBar.isDefaultToolbar(
				ToolBar.getAllToolsNoMacros(true, false, false)));
		assertTrue(ToolBar.isDefaultToolbar(
				ToolBar.getAllToolsNoMacros(true, false, true)));
	}
}
