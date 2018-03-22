package org.geogebra.common.properties.impl.general;

import java.util.Locale;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;
import org.geogebra.common.util.lang.Language;

public class LanguageProperty extends AbstractEnumerableProperty {

    private App app;

    private Locale[] locales;
    private String[] languageCodes;

    public LanguageProperty(App app, Localization localization) {
        super(localization, "Language");
        this.app = app;
        setupValues(app, localization);
    }

    private void setupValues(App app, Localization localization) {
        Language[] languages = localization.getSupportedLanguages(
                app.has(Feature.ALL_LANGUAGES));
        String[] values = new String[languages.length];
        languageCodes = new String[languages.length];
        for (int i = 0; i < languages.length; i++) {
            Language language = languages[i];
            values[i] = language.name;
            languageCodes[i] = language.getLocaleGWT();
        }
        locales = localization.getLocales(languages);
        setValues(values);
    }

    @Override
    protected void setValueSafe(String value, int index) {
        app.setLanguage(languageCodes[index]);
    }

    @Override
    public int getCurrent() {
        Localization localization = getLocalization();
        Locale locale = localization.getLocale();
        for (int i = 0; i < locales.length; i++) {
            if (locales[i].equals(locale)) {
                return i;
            }
        }
        String language = localization.getLocaleStr();
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].startsWith(language)) {
                return i;
            }
        }
        return NONE;
    }
}
