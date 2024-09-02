package org.geogebra.common.properties.impl.general;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        setNamedValues(Arrays.stream(languages)
                .map(language -> entry(language.toLanguageTag(), language.name))
                .collect(Collectors.toList()));
    }

    @Override
    protected void doSetValue(String value) {
        app.setLanguage(value);
    }

    @Override
    public String getValue() {
        return getLocalization().getPreferredLanguageTag();
    }

    @Override
    public boolean isEnabled() {
        return !GlobalScope.examController.isExamActive();
    }
}
