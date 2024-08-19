package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.lang.reflect.Field;
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
		String pathname = "../../manual/en/modules/ROOT/pages/tools";
		File manual = new File(pathname);
		assumeTrue(manual.isDirectory());
		StringBuilder missing = new StringBuilder();
		for (int mode: modes) {
			String modeText = EuclidianConstants.getModeText(mode);
			if (!StringUtil.empty(modeText) && !EuclidianConstants.isNotesTool(mode)
					&& mode != EuclidianConstants.MODE_PROBABILITY_CALCULATOR
					&& mode != EuclidianConstants.MODE_PHOTO_LIBRARY) {
				String english = EuclidianConstants.getModeHelpPageSimple(mode);
				if (!new File(pathname + "/" + english + ".adoc").exists()) {
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
		assertEquals("Graspable Math, PDF, H5P", missing);
	}

}
