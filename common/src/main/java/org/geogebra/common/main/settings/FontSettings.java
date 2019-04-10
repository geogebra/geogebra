package org.geogebra.common.main.settings;

public class FontSettings {

	private int appFontSize;
	private int guiFontSize;

	protected FontSettings(DefaultSettings defaultSettings) {
		appFontSize = defaultSettings.getAppFontSize();
		guiFontSize = defaultSettings.getGuiFontSize();
	}

	public int getAppFontSize() {
		return appFontSize;
	}

	public void setAppFontSize(int appFontSize) {
		this.appFontSize = appFontSize;
	}

	public int getGuiFontSize() {
		return guiFontSize;
	}

	public void setGuiFontSize(int guiFontSize) {
		this.guiFontSize = guiFontSize;
	}

	public void resetGuiFontSize() {
		guiFontSize = -1;
	}

	public int getAlgebraFontSize() {
		return getAppFontSize() + 2;
	}
}
