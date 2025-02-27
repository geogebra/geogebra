package org.geogebra.common.main.color;

import org.geogebra.common.awt.GColor;

public enum GeoColorValues implements ColorValues {

	BLACK_900(GColor.newColorRGB(0x1C1C1F)),
	BLACK_700(GColor.newColorRGB(0x6E6D73)),
	BLACK_500(GColor.newColorRGB(0xB4B3BA)),
	BLACK_400(GColor.newColorRGB(0xD1D0D6)),
	BLACK_200(GColor.newColorRGB(0xF3F2F7)),
	WHITE(GColor.WHITE),
	CUSTOM_COLOR(null),
	MEBIS_PURPLE_400(GColor.newColorRGB(0x723B86)),
	PURPLE_700(GColor.newColorRGB(0x5145A8)),
	BLUE_DARK(GColor.newColorRGB(0x143E92)),
	GREEN_DARK(GColor.newColorRGB(0x006758)),
	YELLOW_DARK(GColor.newColorRGB(0xF1C232)),
	ORANGE_DARK(GColor.newColorRGB(0xC75000)),
	RED_DARK(GColor.newColorRGB(0xB00020)),
	MEBIS_PURPLE_300(GColor.newColorRGB(0x975FA8)),
	PURPLE_600(GColor.newColorRGB(0x6557D2)),
	BLUE_MEDIUM(GColor.newColorRGB(0x1565C0)),
	GREEN_MEDIUM(GColor.newColorRGB(0x388C83)),
	YELLOW_MEDIUM(GColor.newColorRGB(0xFFCC02)),
	ORANGE_MEDIUM(GColor.newColorRGB(0xE07415)),
	RED_MEDIUM(GColor.newColorRGB(0xD32F2F)),
	MEBIS_PURPLE_100(GColor.newColorRGB(0xEADFEE)),
	PURPLE_100(GColor.newColorRGB(0xF3F0FF)),
	BLUE_LIGHT(GColor.newColorRGB(0xCAD5ED)),
	GREEN_LIGHT(GColor.newColorRGB(0xD2E4E2)),
	YELLOW_LIGHT(GColor.newColorRGB(0xFFF2CC)),
	ORANGE_LIGHT(GColor.newColorRGB(0xF5E0D2)),
	RED_LIGHT(GColor.newColorRGB(0xF1D2D8));

	private final GColor color;

	GeoColorValues(GColor color) {
		this.color = color;
	}

	@Override
	public GColor getColor() {
		return color;
	}
}
