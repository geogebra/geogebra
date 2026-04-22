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

package org.geogebra.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.jre.headless.Utf8Control;

public class LocalizationCommonUTF extends LocalizationCommon {
	private static final Map<String, String> missingTranslations = new HashMap<>();

	/**
	 * @param dimension 3 for 3D
	 */
	public LocalizationCommonUTF(int dimension) {
		super(dimension);
		setResourceBundleControl(new Utf8Control());
	}

	@Override
	public boolean hasAllLanguages() {
		return true;
	}

	@Override
	protected void reportMissing(String key, String fallback) {
		if (key.startsWith("_") || List.of(
				"InlineText", "Function.npr", "Function.ncr",
						"Name.l", "Name.f", "Name.m", "Name.shape").contains(key)) {
			return;
		}
		missingTranslations.put(key, fallback);
		String allMissing = "[\n" + missingTranslations.entrySet().stream()
				.map(entry -> "{\"key\":\"" + entry.getKey()
						+ "\",\"default\":\"" + entry.getValue() + "\"}")
				.collect(Collectors.joining(",\n")) + "\n]";
		try {
			Files.writeString(Path.of("build/missing-translations"), allMissing);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (System.getenv("COLLECT_TRANSLATIONS") == null) {
			throw new AssertionError("Key not found " + key + "; fallback is " + fallback);
		}
	}
}
