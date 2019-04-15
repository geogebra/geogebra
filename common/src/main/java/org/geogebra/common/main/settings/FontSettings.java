package org.geogebra.common.main.settings;

public class FontSettings implements Resetable {

	private DefaultSettings defaultSettings;
	private int appFontSize;
	private int guiFontSize;

	protected FontSettings(DefaultSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
		initSizes();
	}

	private void initSizes() {
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

	@Override
	public void resetDefaults() {
		initSizes();
	}
}
