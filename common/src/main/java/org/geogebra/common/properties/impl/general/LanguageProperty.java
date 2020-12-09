package org.geogebra.common.properties.impl.general;

import java.util.Locale;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.util.lang.Language;

import com.google.j2objc.annotations.Weak;

/**
 * Property for setting the language.
 */
public class LanguageProperty extends AbstractEnumerableProperty {

    @Weak
    private App app;
    private OnLanguageSetCallback onLanguageSetCallback;

    private Locale[] locales;
    private String[] languageCodes;

	public interface OnLanguageSetCallback {
		public void run(String lang);
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
        String lang = languageCodes[index];
        app.setLanguage(lang);
        if (onLanguageSetCallback != null) {
            onLanguageSetCallback.run(lang);
        }
    }

    @Override
    public int getIndex() {
        Localization localization = getLocalization();
        Locale locale = localization.getLocale();
        for (int i = 0; i < locales.length; i++) {
            if (locales[i] != null && locales[i].equals(locale)) {
                return i;
            }
        }
        String language = localization.getLocaleStr();
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].startsWith(language)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isEnabled() {
        return !(app.isExam() && app.isExamStarted());
    }
}
