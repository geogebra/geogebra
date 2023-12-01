package org.geogebra.deslktop.gui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.desktop.gui.util.JSVGImageBuilder;
import org.geogebra.desktop.gui.util.SVGImage;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.ImageManagerD;
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

	public static final String RESOURCES = "src/test/resources/svg/";

	@Test
	public void testLoadSvgsGGB() {
		loadGGB("svgs.ggb", 100);
	}

	private void loadGGB(String fileName, int count) {
		byte[] array = UtilD.loadFileIntoByteArray(RESOURCES + fileName);
		assertNotNull("File error: " + fileName, array);
		assertTrue(app.loadXML(new ByteArrayZipFile(array)));
		assertEquals(count, ((ImageManagerD) app.getImageManager()).getImageCount());
	}

	@Test
	public void testLoadVrTGGB() {
		loadGGB("material-VrT75QCK.ggb", 1);
	}

	@Test
	public void testLoadWhiteListIssue() {
		loadSvg("issue41.svg");
	}

	private static void loadSvg(String svg) {
		try {
			image = JSVGImageBuilder.fromFile(new File(RESOURCES + svg));
		} catch (IOException e) {
			fail(e.getMessage());
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
	public void loadBadLink() {
		loadSvg("badLink.svg");
	}
}
