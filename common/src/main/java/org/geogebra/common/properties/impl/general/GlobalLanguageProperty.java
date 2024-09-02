package org.geogebra.common.properties.impl.general;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.util.OrderedCollector;
import org.geogebra.common.util.lang.Language;

public class GlobalLanguageProperty extends AbstractNamedEnumeratedProperty<String> {

	private String value;

	/**
	 * Create a new instance.
	 * @param localization The localization.
	 */
	public GlobalLanguageProperty(@Nonnull Localization localization) {
		super(localization, "Language");
		setupValues(localization);
	}

	private void setupValues(Localization localization) {
		setNamedValues(Arrays.stream(localization.getSupportedLanguages(false)).collect(
				OrderedCollector.toMap(Language::toLanguageTag, Enum::name)));
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	protected void doSetValue(String value) {
		this.value = value;
	}
}
