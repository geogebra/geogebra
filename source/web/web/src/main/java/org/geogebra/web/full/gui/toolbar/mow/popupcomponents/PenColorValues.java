package org.geogebra.web.full.gui.toolbar.mow.popupcomponents;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.color.ColorValues;

public enum PenColorValues implements ColorValues {

	BLACK(GColor.newColorRGB(0x1C1C1F)),
	MEBIS_PURPLE(GColor.newColorRGB(0x975FA8)),
	GGB_PURPLE(GColor.DEFAULT_PURPLE),
	BLUE(GColor.newColorRGB(0x1565C0)),
	GREEN(GColor.newColorRGB(0x388C83)),
	WHITE(GColor.WHITE),
	YELLOW(GColor.newColorRGB(0xFFCC02)),
	ORANGE(GColor.newColorRGB(0xE07415)),
	RED(GColor.newColorRGB(0xD32F2F)),
	CUSTOM_COLOR(null);

	private final GColor color;

	PenColorValues(GColor color) {
		this.color = color;
	}

	@Override
	public GColor getColor() {
		return color;
	}
}