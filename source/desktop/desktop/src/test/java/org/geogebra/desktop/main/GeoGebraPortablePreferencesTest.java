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

package org.geogebra.desktop.main;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.GeoGebraPreferences;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GeoGebraPortablePreferencesTest {

	@TempDir
	Path tmp;

	/**
	 * Portable mode (--settingsFile) must save and load construction defaults
	 * (point size, line thickness, colors) also for the 3D desktop app.
	 */
	@Test
	void savesAndLoadsObjectDefaultsFor3DApp() throws Exception {
		Path settingsFile = tmp.resolve("geogebra.properties");
		GeoGebraPreferencesD.setPropertyFileName(settingsFile.toString());
		try {
			AppD app = mock3DApp();
			GeoGebraPreferencesD prefs = GeoGebraPreferencesD.getPref();

			prefs.saveXMLPreferences(app);

			Properties stored = new Properties();
			try (InputStream in = Files.newInputStream(settingsFile)) {
				stored.load(in);
			}
			String defaultsXml = stored.getProperty(GeoGebraPreferences.XML_DEFAULT_OBJECT_PREFERENCES);
			assertNotNull(defaultsXml, "object defaults not saved");
			assertTrue(defaultsXml.contains("<defaults"), "defaults XML incomplete");

			// avoid regenerating factory XML from the mock during load
			prefs.factoryDefaultXml = "<factory/>";

			prefs.loadXMLPreferences(app);

			verify(app).setXML(argThat((String xml) -> xml.contains("<defaults")), eq(false));
		} finally {
			GeoGebraPreferencesD.setPropertyFileName(null);
		}
	}

	private static AppD mock3DApp() {
		AppD app = mock(AppD.class);
		Kernel kernel = mock(Kernel.class);
		Construction construction = mock(Construction.class);
		ConstructionDefaults defaults = mock(ConstructionDefaults.class);
		when(app.getKernel()).thenReturn(kernel);
		when(kernel.getConstruction()).thenReturn(construction);
		when(construction.getConstructionDefaults()).thenReturn(defaults);
		when(app.is3D()).thenReturn(true);
		when(app.getPreferencesXML()).thenReturn("<geogebra/>");
		when(app.getMacroFileAsByteArray()).thenReturn(new byte[] {1, 2});
		doAnswer(invocation -> {
					XMLStringBuilder sb = invocation.getArgument(0);
					sb.startOpeningTag("defaults", 0);
					sb.closeTag("defaults");
					return null;
				})
				.when(defaults)
				.getDefaultsXML(org.mockito.ArgumentMatchers.any(XMLStringBuilder.class));
		return app;
	}
}
