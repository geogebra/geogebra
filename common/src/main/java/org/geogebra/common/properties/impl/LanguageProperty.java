package org.geogebra.common.properties.impl;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;
import org.geogebra.common.util.lang.Language;

import java.util.ArrayList;
import java.util.List;

public class LanguageProperty extends AbstractEnumerableProperty {

    private App app;
    private List<String> languageCodes = new ArrayList<>();

    public LanguageProperty(App app, Localization localization) {
        super(localization, "Language");
        this.app = app;
        setupValues();
    }

    private void setupValues() {
        List<String> languages = new ArrayList<>();
        for (Language language: Language.values()) {
            if (language.fullyTranslated || app.has(Feature.ALL_LANGUAGES)) {
                languages.add(language.name);
                languageCodes.add(language.getLocaleGWT());
            }
        }

        String[] values = new String[languages.size()];
        values = languages.toArray(values);
        setValues(values);
    }

    @Override
    protected void setValueSafe(String value, int index) {
        app.setLanguage(languageCodes.get(index));
    }

    @Override
    public int getCurrent() {
        Localization localization = getLocalization();
        String language = localization.getLocaleStr();
        for (int i = 0; i < languageCodes.size(); i++) {
            if (languageCodes.get(i).startsWith(language)) {
                return i;
            }
        }
        return 0;
    }
}
