package org.geogebra.common.properties.impl.general;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.util.Util;

/**
 * Property representing the font size.
 */
public class FontSizeProperty extends AbstractEnumerableProperty {

    private App app;

    /**
     * Constructs a font size property.
     *
     * @param app          app
     * @param localization localization
     */
    public FontSizeProperty(App app, Localization localization) {
        super(localization, "FontSize");
        this.app = app;

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
        app.setFontSize(fontSize, true);
    }

    @Override
    public int getIndex() {
        int fontSize = app.getFontSize();
        for (int i = 0; i < Util.menuFontSizesLength(); i++) {
            if (Util.menuFontSizes(i) == fontSize) {
                return i;
            }
        }
        return -1;
    }
}
