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

package org.geogebra.web.html5.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.TreeSet;

import org.geogebra.common.util.lang.Language;
import org.junit.Test;

public class LocalizationWTest {

	@Test
	public void gwtTranslationFilesShouldMatchLanguages() {
		File dir = new File("src/main/resources/org/geogebra/web/pub/js/");
		TreeSet<String> available = new TreeSet<>();
		for (File f : dir.listFiles()) {
			if (f.getName().contains("properties_")) {
				available.add(f.getAbsolutePath());
			}
		}
		for (Language lang : Language.values()) {
			File trans = new File("src/main/resources/org/geogebra/web/pub/js/properties_keys_"
							+ lang.toLanguageTag() + ".js");
			assertTrue(trans.getAbsolutePath(),
					available.remove(trans.getAbsolutePath()));

		}
		StringBuilder sb = new StringBuilder();
		for (String fn : available) {
			sb.append(fn).append("\n");
		}
		assertEquals("", sb.toString());
	}
}
