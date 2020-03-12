package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.awt.GColor;

public enum MOWToolbarColor {

	BLACK(GColor.BLACK, "black"),
	GREEN(getGColFromHex(0x2E7D32), "green"),
	TEAL(getGColFromHex(0x00A8A8), "teal"),
	BLUE(getGColFromHex(0x1565C0), "blue"),
	PURPLE(getGColFromHex(0x6557D2), "purple"),
	PINK(getGColFromHex(0xCC0099), "pink"),
	RED(getGColFromHex(0xD32F2F), "red"),
	ORANGE(getGColFromHex(0xDB6114), "orange"),
	YELLOW(getGColFromHex(0xFFCC00), "yellow");

	private final GColor color;
	private final String ggbTransKey;

	MOWToolbarColor(GColor color, String ggbTransKey) {
		this.color = color;
		this.ggbTransKey = ggbTransKey;
	}

	private static GColor getGColFromHex(int colorHexCode) {
		return GColor.newColorRGB(colorHexCode);
	}

	public GColor getGColor() {
		return color;
	}

	public String getGgbTransKey() {
		return ggbTransKey;
	}
}
