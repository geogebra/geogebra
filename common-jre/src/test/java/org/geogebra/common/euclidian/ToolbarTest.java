package org.geogebra.common.euclidian;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.junit.Assert;
import org.junit.Test;

public class ToolbarTest {
	@Test
	public void testToolbar() {
		String def = "0 39 73 62 | 1 501 67 , 5 19 , 72 75 76 | 2 15 45 , 18 65 , 7 37 | 4 3 8 9 , 13 44 , 58 , 47 | 16 51 64 , 70 | 10 34 53 11 , 24  20 22 , 21 23 | 55 56 57 , 12 | 36 46 , 38 49  50 , 71  14  68 | 30 29 54 32 31 33 | 25 17 26 60 52 61 | 40 41 42 , 27 28 35 , 6";
		Assert.assertTrue(ToolBar.isDefaultToolbar(def));
		Assert.assertTrue(ToolBar.isDefaultToolbar(ToolBar.getAllToolsNoMacros(
				false, false, false)));
		Assert.assertTrue(ToolBar.isDefaultToolbar(
				ToolBar.getAllToolsNoMacros(true, false, false)));
		Assert.assertTrue(ToolBar.isDefaultToolbar(
				ToolBar.getAllToolsNoMacros(true, false, true)));
	}
}
