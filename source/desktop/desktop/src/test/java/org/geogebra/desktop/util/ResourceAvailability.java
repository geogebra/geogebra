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
 
package org.geogebra.desktop.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.geogebra3D.gui.GuiResources3D;
import org.junit.Test;

public class ResourceAvailability {

	@Test
	public void checkGuiResources() {
		for (GuiResourcesD res : GuiResourcesD.values()) {
			String fn = res.getFilename();
			if (!fn.startsWith("/org")) {
				fn = "/org/geogebra/desktop/" + fn;
			}
			URL url = ResourceAvailability.class.getResource(fn);
			assertNotNull("" + res, url);
		}
	}

	@Test
	public void checkGuiResources3D() {
		for (GuiResources3D res : GuiResources3D.values()) {
			String fn = res.getFilename();
			if (!fn.startsWith("/org")) {
				fn = "/org/geogebra/desktop/geogebra3D" + fn;
			}
			URL url = ResourceAvailability.class.getResource(fn);
			assertNotNull("" + res, url);
		}
	}

	@Test
	public void noDupes() {
		assertNoDupesRecursive("src/main/resources", "src/gpl/resources");
		assertNoDupesRecursive("src/main/resources", "src/nonfree/resources");
	}

	private void assertNoDupesRecursive(String f1, String f2) {
		for (String fn: new File(f1).list()) {
			if (new File(f1 + "/" + fn).isDirectory()) {
				assertNoDupesRecursive(f1 + "/" + fn, f2 + "/" + fn);
			} else {
				assertFalse("Duplicate found:" + f2 + "/" + fn,
						new File(f2 + "/" + fn).exists());
			}
		}
	}

	@Test
	public void checkToolIcons() {
		StringUtil.setPrototypeIfNull(new StringUtilD());
		ImageManagerD man = new ImageManagerD(new JPanel());
		StringBuilder missing = new StringBuilder();

		for (int i = 0; i < 3000; i++) {
			String modeText = EuclidianConstants.getModeTextSimple(i);

			if (modeText.isEmpty() || EuclidianConstants.isNotesTool(i)) {
				continue;
			}
			switch (i) {
			case EuclidianConstants.MODE_SELECTION_LISTENER:
			case EuclidianConstants.MODE_PHOTO_LIBRARY:
				continue;
			default:
				ImageResourceD res = man.getToolImageResource(modeText);
				URL url = ResourceAvailability.class.getResource(res
						.getFilename());
				if (url == null) {
					missing.append(res.getFilename()).append(",");
				}
			}

		}

		assertEquals(missing.toString(), 0, missing.length());
	}

	@Test
	public void imageSetsShouldBeIdentical() {
		String commonSrc = "../../shared/common/src";
		String lowres = Arrays.stream(new File(
				commonSrc + "/nonfree/resources/org/geogebra/common/icons_toolbar/p32/")
				.list()).sorted().collect(
				Collectors.joining("\n"));
		String[] otherSets = new String[] {
				commonSrc + "/nonfree/resources/org/geogebra/common/icons_toolbar/p64/",
				commonSrc + "/gpl/resources/org/geogebra/common/icons_toolbar/p32/",
				commonSrc + "/gpl/resources/org/geogebra/common/icons_toolbar/p32/"};
		for (String directory: otherSets) {
			String hires = Arrays.stream(new File(directory).list()).sorted().collect(
					Collectors.joining("\n"));
			assertEquals(directory, lowres, hires);
		}
	}
}
