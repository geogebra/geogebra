package org.geogebra.common.properties.impl.general;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.common.main.settings.updater.FontSettingsUpdater;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.util.Util;

/**
 * Property representing the font size.
 */
public class FontSizeProperty extends AbstractEnumerableProperty {

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
        String[] values = new String[Util.menuFontSizesLength()];
        for (int i = 0; i < Util.menuFontSizesLength(); i++) {
            int fontSize = Util.menuFontSizes(i);
            values[i] = localization.getPlain("Apt", Integer.toString(fontSize));
        }
        setValues(values);
    }

    @Override
    protected void setValueSafe(String value, int index) {
        int fontSize = Util.menuFontSizes(index);
        fontSettingsUpdater.setAppFontSizeAndUpdateViews(fontSize);
    }

    @Override
    public int getIndex() {
        int fontSize = fontSettings.getAppFontSize();
        for (int i = 0; i < Util.menuFontSizesLength(); i++) {
            if (Util.menuFontSizes(i) == fontSize) {
                return i;
            }
        }
        return -1;
    }
}
