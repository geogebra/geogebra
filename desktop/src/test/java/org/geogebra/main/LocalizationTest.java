package org.geogebra.main;

import java.io.File;
import java.util.TreeSet;

import org.geogebra.common.util.lang.Language;
import org.junit.Assert;
import org.junit.Test;

public class LocalizationTest {
	@Test
	public void gwtLocalesTest() {
		File dir = new File(
				"../web/src/nonfree/resources/org/geogebra/web/pub/js/");
		TreeSet<String> available = new TreeSet<>();
		for (File f : dir.listFiles()) {
			available.add(f.getAbsolutePath());
		}
		for (Language lang : Language.values()) {
			File trans = new File(
					"../web/src/nonfree/resources/org/geogebra/web/pub/js/properties_keys_"
							+ lang.getLocaleGWT() + ".js");
			Assert.assertTrue(trans.getAbsolutePath(),
					available.remove(trans.getAbsolutePath()));

		}
		StringBuilder sb = new StringBuilder();
		for (String fn : available) {
			sb.append(fn + "\n");
		}
		Assert.assertEquals("", sb.toString());
	}
}
