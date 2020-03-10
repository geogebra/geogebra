package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.awt.GColor;

public enum MOWToolbarColor {

	BLACK(GColor.BLACK, "ToolbarColor.Black"),
	GREEN(getGColFromHex(0x2E7D32), "ToolbarColor.Green"),
	TEAL(getGColFromHex(0x00A8A8), "ToolbarColor.Teal"),
	BLUE(getGColFromHex(0x1565C0), "ToolbarColor.Blue"),
	PURPLE(getGColFromHex(0x6557D2), "ToolbarColor.Purple"),
	PINK(getGColFromHex(0xCC0099), "ToolbarColor.Pink"),
	RED(getGColFromHex(0xD32F2F), "ToolbarColor.Red"),
	ORANGE(getGColFromHex(0xDB6114), "ToolbarColor.Orange"),
	YELLOW(getGColFromHex(0xFFCC00), "ToolbarColor.Yellow");

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
