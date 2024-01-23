package org.geogebra.common.properties.impl.general;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.util.lang.Language;

import com.google.j2objc.annotations.Weak;

/**
 * Property for setting the language.
 */
public class LanguageProperty extends AbstractNamedEnumeratedProperty<String> {

    @Weak
    private final App app;
    private OnLanguageSetCallback onLanguageSetCallback;

    private String[] languageCodes;

	public interface OnLanguageSetCallback {
		void run(String lang);
	}

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

    /**
     * Constructs a language property.
     *
     * @param app          app
     * @param onLanguageSetCallback called when language changed
     * @param localization localization
     */
	public LanguageProperty(App app, Localization localization,
			OnLanguageSetCallback onLanguageSetCallback) {
        this(app, localization);
        this.onLanguageSetCallback = onLanguageSetCallback;
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
        if (onLanguageSetCallback != null) {
            onLanguageSetCallback.run(value);
        }
    }

    @Override
    public String getValue() {
        return getLocalization().getLanguageTag();
    }

    @Override
    public boolean isEnabled() {
        return !(app.isExam() && app.isExamStarted());
    }
}
