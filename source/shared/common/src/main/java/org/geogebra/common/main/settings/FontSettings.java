package org.geogebra.common.main.settings;

/**
 * Font settings.
 */
public class FontSettings implements Resettable {

	private DefaultSettings defaultSettings;
	private int appFontSize;
	private int guiFontSize;

	/**
	 * This constructor is protected because it should be called only by the SettingsBuilder.
	 * @param defaultSettings default settings
	 */
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

	/**
	 * @return If the gui font size is not initialized (the value is -1)
	 * then returns the app font size,
	 * otherwise returns the gui font size.
	 */
	public int getGuiFontSizeSafe() {
		return guiFontSize == -1 ? appFontSize : guiFontSize;
	}

	public void setGuiFontSize(int guiFontSize) {
		this.guiFontSize = guiFontSize;
	}

	public int getAlgebraFontSize() {
		return getAppFontSize() + 2;
	}

	@Override
	public void resetDefaults() {
		initSizes();
	}
}
