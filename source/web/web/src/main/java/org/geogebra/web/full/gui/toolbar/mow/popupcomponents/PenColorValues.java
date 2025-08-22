package org.geogebra.web.full.gui.toolbar.mow.popupcomponents;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.GeoGebraColorConstants;

public final class PenColorValues  {

	/**
	 * @return colors for pen tool
	 */
	public static List<GColor> values() {
		return Arrays.asList(
				GeoGebraColorConstants.GEOGEBRA_OBJECT_BLACK,
				GColor.newColorRGB(0x975FA8),
				GColor.DEFAULT_PURPLE,
				GColor.newColorRGB(0x1565C0),
				GColor.newColorRGB(0x388C83),
				GColor.WHITE,
				GColor.newColorRGB(0xFFCC02),
				GColor.newColorRGB(0xE07415),
				GColor.newColorRGB(0xD32F2F),
				null);
	}

	private PenColorValues() {
		// utility class
	}
}