/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl.general;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

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
		setNamedValues(Arrays.stream(localization.getSupportedLanguages(false))
				.map(language -> entry(language.toLanguageTag(), language.name))
				.collect(Collectors.toList()));
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
