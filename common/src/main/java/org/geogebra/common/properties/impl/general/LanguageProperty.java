package org.geogebra.common.properties.impl.general;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.util.lang.Language;

import com.google.j2objc.annotations.Weak;

/**
 * Property for setting the language.
 *
 * Note: Consider using GlobalLanguageProperty instead.
 */
public class LanguageProperty extends AbstractNamedEnumeratedProperty<String> {

    @Weak
    private final App app;

    private String[] languageCodes;

    /**
     * Constructs a language property.
     *
     * @param app          app
     * @param localization localization
     */
    public LanguageProperty(App app, Localization localization) {
        super(localization, "Language");
        this.app = app;
        setupValues(app, localization);
    }

    private void setupValues(App app, Localization localization) {
        Language[] languages = localization.getSupportedLanguages(
                app.has(Feature.ALL_LANGUAGES));
        String[] valueNames = new String[languages.length];
        languageCodes = new String[languages.length];
        for (int i = 0; i < languages.length; i++) {
            Language language = languages[i];
            valueNames[i] = language.name;
            languageCodes[i] = language.toLanguageTag();
        }
        setValues(languageCodes);
        setValueNames(valueNames);
    }

    @Override
    protected void doSetValue(String value) {
        app.setLanguage(value);
    }

    @Override
    public String getValue() {
        return getLocalization().getLanguageTag();
    }

    @Override
    public boolean isEnabled() {
        return !GlobalScope.examController.isExamActive();
    }
}
