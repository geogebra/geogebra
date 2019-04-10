package org.geogebra.common.main.settings;

public class DefaultSettingsCommon implements DefaultSettings {

	private static final int APP_FONT_SIZE = 12;
	private static final int GUI_FONT_SIZE = -1;

	@Override
	public int getAppFontSize() {
		return APP_FONT_SIZE;
	}

	@Override
	public int getGuiFontSize() {
		return GUI_FONT_SIZE;
	}
}
