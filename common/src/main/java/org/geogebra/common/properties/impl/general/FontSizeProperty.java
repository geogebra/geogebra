package org.geogebra.common.properties.impl.general;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
		Map<Integer, String> values = IntStream.range(0, Util.menuFontSizesLength())
				.boxed().collect(Collectors.toMap(
						index -> index,
						index -> localization
								.getPlain("Apt", String.valueOf(Util.menuFontSizes(index)))));
		setNamedValues(values);
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
