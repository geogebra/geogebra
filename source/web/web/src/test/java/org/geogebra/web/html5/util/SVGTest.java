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

package org.geogebra.web.html5.util;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.gwtproject.resources.client.ClientBundle;
import org.junit.Test;

public class SVGTest {

	public static final String BASE = "../../shared/common/src/nonfree/resources/";
	private static final String SVG_PATH = BASE + "org/geogebra/common/icons/svg/web/toolIcons";

	@Test
	public void rmExtraSVGS() {
		Path svgs = Paths.get(SVG_PATH);
		Path root = Paths.get(BASE);
		TreeSet<String> disk = new TreeSet<>();
		try (Stream<Path> svgList = Files.list(svgs)) {
			svgList.map(root::relativize)
					.map(s -> s.toString().replace('\\', '/'))
					.forEach(disk::add);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		Stream.concat(Arrays.stream(ToolbarSvgResources.class.getMethods()),
				Arrays.stream(ToolbarSvgResourcesSync.class.getMethods())).forEach(m -> {
			ClientBundle.Source a = m.getAnnotation(ClientBundle.Source.class);
			if (a != null) {
				String src = a.value()[0];
				disk.remove(src);
			}
		});
		// photolibrary duplicates camera (for mobile)
		// freehandfunction duplicates freehandshape (for mobile)
		assertEquals("mode_freehandfunction.svg,mode_photolibrary.svg", disk.stream()
				.map(s -> new File(s).getName()).collect(Collectors.joining(",")));
	}

	@Test
	public void checkToolIcons() {
		StringBuilder missing = new StringBuilder();

		for (int i = 0; i < 3000; i++) {
			String modeText = EuclidianConstants.getModeTextSimple(i).toLowerCase(Locale.ROOT);

			if (modeText.isEmpty()) {
				continue;
			}
			switch (i) {
			case EuclidianConstants.MODE_SELECTION_LISTENER:
			case EuclidianConstants.MODE_GRASPABLE_MATH:
			case EuclidianConstants.MODE_CALCULATOR:
				continue;
			default:
				File icon = new File(SVG_PATH + "/mode_" + modeText + ".svg");
				if (!icon.exists()) {
					missing.append(modeText).append(",");
				}
			}

		}

		assertEquals("", missing.toString());
	}
}
