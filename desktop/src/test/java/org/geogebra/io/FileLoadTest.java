package org.geogebra.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.AppCommon3D;
import org.junit.Before;
import org.junit.Test;

public class FileLoadTest {
	private AppCommon app;

	@Before
	public void setup() {
		app = new AppCommon3D(new LocalizationCommon(3), new AwtFactoryCommon());
	}

	@Test
	public void quadricsShouldUpdateAfterFileLoad() {
		app.setRandomSeed(7);
		load("src/test/java/org/geogebra/io/quadric.xml");
		GeoQuadric3D quadric = (GeoQuadric3D) app.getKernel().lookupLabel("eq1");
		assertEquals(3.872983346207417,
				quadric.getHalfAxis(0), Kernel.STANDARD_PRECISION);
	}

	private void load(String filename) {
		Path filePath = Paths.get(filename);
		String content = null;
		try {
			content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		app.setXML(content, true);
	}
}
