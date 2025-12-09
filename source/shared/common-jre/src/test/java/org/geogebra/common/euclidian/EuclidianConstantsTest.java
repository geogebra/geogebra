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

package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.util.StringUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class EuclidianConstantsTest extends BaseUnitTest {

	private static Set<Integer> modes = new TreeSet<>();

	/** Collect all modes other than macros */
	@BeforeClass
	public static void collectModes() throws IllegalAccessException {
		Field[] fields = EuclidianConstants.class
			.getFields();

		for (Field f : fields) {
			if (f.getName().startsWith("MODE_") && !"MODE_MACRO".equals(f.getName())) {
				modes.add(f.getInt(null));
			}
		}
	}

	@Test
	public void allModesShouldHaveHelpPage() {
		String pathname = "../../../../manual/en/modules/ROOT/pages/tools";
		File manual = new File(pathname);
		assumeTrue(manual.isDirectory());
		StringBuilder missing = new StringBuilder();
		List<String> files = Arrays.asList(Objects.requireNonNull(manual.list()));
		for (int mode: modes) {
			String modeText = EuclidianConstants.getModeText(mode);
			if (!StringUtil.empty(modeText) && !EuclidianConstants.isNotesTool(mode)
					&& mode != EuclidianConstants.MODE_PROBABILITY_CALCULATOR
					&& mode != EuclidianConstants.MODE_PHOTO_LIBRARY) {
				String english = EuclidianConstants.getModeHelpPage(mode);
				if (!files.contains(english + ".adoc")) {
					missing.append(english).append(", ");
				}
			}
		}
		assertEquals("", missing.toString());
	}

	@Test
	public void allModesShouldHaveHelp() {
		String missing = modes.stream()
				.filter(Predicate.not(EuclidianConstants::isNotesTool))
				.map(EuclidianConstants::getHelpTransKey)
				.filter(Predicate.not(getApp().getLocalization()::hasMenu))
				.collect(Collectors.joining(", "));
		assertEquals("PhotoLibrary.Help", missing);
	}

	@Test
	public void allModesShouldHaveName() {
		String missing = modes.stream().map(EuclidianConstants::getModeText)
				.filter(Predicate.not(getApp().getLocalization()::hasMenu))
				.collect(Collectors.joining(", "));
		assertEquals("Graspable Math, PDF", missing);
	}

}
