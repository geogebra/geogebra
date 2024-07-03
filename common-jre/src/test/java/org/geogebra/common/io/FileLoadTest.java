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
