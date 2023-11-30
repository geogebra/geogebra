package org.geogebra.deslktop.gui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.desktop.gui.util.JSVGImageBuilder;
import org.geogebra.desktop.gui.util.SVGImage;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Before;
import org.junit.Test;

public class SvgLoadTest extends BaseUnitTest {

	static AppDNoGui app;
	private static SVGImage image;

	@Before
	public void setUp() {
		app = new AppDNoGui(new LocalizationD(3), false);
	}

	public static final String E2E_RESOURCES = "src/e2eTest/resources/svg/";

	@Test
	public void testLoadSvgsGGB() {
		loadGGB("svgs.ggb");
	}

	private void loadGGB(String fileName) {
		byte[] array = UtilD.loadFileIntoByteArray(E2E_RESOURCES + fileName);
		assertNotNull("File error: " + fileName, array);
		assertTrue(app.loadXML(new ByteArrayZipFile(array)));

	}

	@Test
	public void testLoadVrTGGB() {
		loadGGB("material-VrT75QCK.ggb");
	}

	@Test
	public void testLoadWhiteListIssue() {
		loadSvg("issue41.svg");
	}

	private static void loadSvg(String svg) {
		try {
			image = JSVGImageBuilder.fromFile(new File(E2E_RESOURCES + svg));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testLoad2() {
		loadSvg("2.svg");
	}

	@Test
	public void imageReloadTest() {
		loadSvg("2.svg");
		SVGImage image2 = JSVGImageBuilder.fromContent(image.getContent());
		assertEquals(image, image2);
	}

	@Test
	public void name() {
		loadSvg("badLink.svg");
	}
}
