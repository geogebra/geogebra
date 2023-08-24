package org.geogebra.common.properties.impl.general;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.common.main.settings.updater.FontSettingsUpdater;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.util.Util;

/**
 * Property representing the font size.
 */
public class FontSizeProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private FontSettings fontSettings;
	private FontSettingsUpdater fontSettingsUpdater;

	/**
	 * @param localization localization
	 * @param fontSettings font settings
	 * @param fontSettingsUpdater font settings updater
	 */
	public FontSizeProperty(
			Localization localization,
			FontSettings fontSettings, FontSettingsUpdater fontSettingsUpdater) {
		super(localization, "FontSize");
		this.fontSettings = fontSettings;
		this.fontSettingsUpdater = fontSettingsUpdater;

		setupValues(localization);
	}

	private void setupValues(Localization localization) {
		Integer[] values = new Integer[Util.menuFontSizesLength()];
		for (int i = 0; i < Util.menuFontSizesLength(); i++) {
			values[i] = Util.menuFontSizes(i);
		}
		setValues(values);

		String[] valueNames = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			valueNames[i] = localization.getPlain("Apt", Integer.toString(values[i]));
		}
		setValueNames(valueNames);
	}

	@Override
	protected void doSetValue(Integer value) {
		fontSettingsUpdater.setAppFontSizeAndUpdateViews(value);
	}

	@Override
	public Integer getValue() {
		return fontSettings.getAppFontSize();
	}
}
