package org.geogebra.desktop.gui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Before;
import org.junit.Test;

public class SvgLoadTest extends BaseUnitTest {

	static AppDNoGui app;
	private static SVGImage image;
	private int allSvgCount = 0;
	private final List<String> unsupportedSvgNames = new ArrayList<>();

	@Before
	public void setUp() {
		allSvgCount = 0;
		unsupportedSvgNames.clear();
		app = new AppDNoGui(new LocalizationD(3), false) {
			@Override
			public void addExternalImage(String name, MyImageJre img) {
				if (img.isSVG()) {
					if (JSVGConstants.UNSUPPORTED_SVG.equals(img.getSVG())) {
						unsupportedSvgNames.add(name.split("/")[1]);
					}
					allSvgCount++;
				} else {
					assertEquals("geogebra_thumbnail.png", name);
				}
			}
		};
	}

	public static final String RESOURCES = "src/test/resources/svg/";

	@Test
	public void testLoadSvgsGGB() {
		inMaterial("svgs.ggb")
				.shouldSupportAllBut("issue55.svg,issue82.svg,issue134.svg")
				.ofSvgs(142);
	}

	private SvgLoadTest inMaterial(String fileName) {
		byte[] array = UtilD.loadFileIntoByteArray(RESOURCES + fileName);
		assertNotNull("File error: " + fileName, array);
		assertTrue(app.loadXML(new ByteArrayZipFile(array)));
		return this;
	}

	private SvgLoadTest shouldSupportAllBut(String unsupported) {
		assertEquals(unsupported, String.join(",", unsupportedSvgNames));
		return this;
	}

	private SvgLoadTest ofSvgs(int all) {
		assertEquals(all, allSvgCount);
		return this;
	}

	@Test
	public void testLoadVrTGGB() {
		inMaterial("material-VrT75QCK.ggb")
				.shouldSupportAllBut("plot (1).svg")
				.ofSvgs(5);
	}

	@Test
	public void testLoadWhiteListIssue() {
		checkSupported("issue41.svg");
	}

	private void checkSupported(String fileName) {
		createSvg(fileName);
		assertNotEquals(JSVGImageBuilder.unsupportedImage(), image);
	}

	private SvgLoadTest createSvg(String svg) {
		try {
			image = JSVGImageBuilder.fromFile(new File(RESOURCES + svg));
		} catch (IOException e) {
			fail(e.getMessage());
		}
		return this;
	}

	@Test
	public void testLoad2() {
		checkSupported("2.svg");
	}

	@Test
	public void imageReloadTest() {
		createSvg("2.svg");
		SVGImage image2 = JSVGImageBuilder.fromContent(image.getContent());
		assertEquals(image, image2);
	}

	@Test
	public void loadBadLink() {
		checkNotSupported("badLink.svg");
	}

	private void checkNotSupported(String fileName) {
		createSvg(fileName);
		assertEquals(JSVGImageBuilder.unsupportedImage(), image);
	}
}