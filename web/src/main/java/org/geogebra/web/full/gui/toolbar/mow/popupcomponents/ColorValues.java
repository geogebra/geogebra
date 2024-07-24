package org.geogebra.web.full.gui.toolbar.mow.popupcomponents;

import org.geogebra.common.awt.GColor;

public enum ColorValues {

	BLACK(GColor.BLACK),
	MEBIS_PURPLE(GColor.newColorRGB(0x975FA8)),
	GGB_PURPLE(GColor.DEFAULT_PURPLE),
	BLUE(GColor.newColorRGB(0x1665C0)),
	GREEN(GColor.newColorRGB(0x388C83)),
	WHITE(GColor.WHITE),
	YELLOW(GColor.newColorRGB(0xFFCC02)),
	ORANGE(GColor.newColorRGB(0xE07415)),
	RED(GColor.newColorRGB(0xD3302F));

	private final GColor color;

	ColorValues(GColor color) {
		this.color = color;
	}

	public GColor getColor() {
		return color;
	}
}