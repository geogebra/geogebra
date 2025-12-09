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

package org.geogebra.common.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Kernel;
import org.junit.Test;

public class FileLoadTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void quadricsShouldUpdateAfterFileLoad() throws IOException {
		getApp().setRandomSeed(7);
		String xml = load("quadric.xml");
		getApp().setXML(xml, true);
		GeoQuadric3D quadric = (GeoQuadric3D) lookup("eq1");
		assertEquals(3.872983346207417,
				quadric.getHalfAxis(0), Kernel.STANDARD_PRECISION);
	}

	@Test
	public void edgesCreatedWhenLoadedFromOldFile() throws IOException, XMLParseException {
		String xml = load("polygon-randomization.xml");
		String macros = load("polygon-macros.xml");
		getApp().setRandomSeed(7);
		getApp().getXMLio().processXMLString(macros, true, true);
		getApp().setXML(xml, false);
		assertThat(lookup("SimMove"), hasValue("(11.3, 2.01)"));
		add("RunClickScript(scriptResetApp)");
		assertThat(lookup("SimMove"), hasValue("(11.4519, 1.4169)"));
	}

	private String load(String filename) throws IOException {
		Path filePath = Paths.get("src/test/resources", filename);
		return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
	}
}
