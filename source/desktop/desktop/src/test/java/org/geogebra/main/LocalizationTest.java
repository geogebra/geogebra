package org.geogebra.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Locale;
import java.util.TreeSet;

import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.util.lang.Language;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.Test;

public class LocalizationTest {
	@Test
	public void gwtTranslationFilesShouldMatchLanguages() {
		File dir = new File(
				"../../web/web/src/nonfree/resources/org/geogebra/web/pub/js/");
		TreeSet<String> available = new TreeSet<>();
		for (File f : dir.listFiles()) {
			if (!f.getAbsolutePath().contains(".svn")) {
				available.add(f.getAbsolutePath());
			}
		}
		for (Language lang : Language.values()) {
			File trans = new File(
					"../../web/web/src/nonfree/resources/org/geogebra/web/pub/js/properties_keys_"
							+ lang.toLanguageTag() + ".js");
			assertTrue(trans.getAbsolutePath(),
					available.remove(trans.getAbsolutePath()));

		}
		StringBuilder sb = new StringBuilder();
		for (String fn : available) {
			sb.append(fn).append("\n");
		}
		assertEquals("", sb.toString());
	}

	@Test
	public void aliasesShouldBeRecognized() {
		checkAlias(Language.Hebrew, "he", "iw");
		checkAlias(Language.Norwegian_Bokmal, "no", "nb", "nb_NO", "no-NO",
				"no_NO");
		checkAlias(Language.Norwegian_Nynorsk, "nn", "no-NO-NY", "nn-NO");
		checkAlias(Language.Chinese_Simplified, "zh", "zh-Hans-CN", "zh-CN");
		checkAlias(Language.Chinese_Traditional, "zh_TW", "zh-Hant-TW",
				"zh-TW");
		checkAlias(Language.Indonesian, "id", "in");
		checkAlias(Language.Filipino, "fil", "tl");
		checkAlias(Language.Yiddish, "yi", "ji");
		checkAlias(Language.Mongolian, "mn", "mn-mn");
		// mn-mn-MT was a GeoGebra-specific name, no longer supported
		checkAlias(Language.Mongolian_Traditional, "mn-Mong");
		checkAlias(Language.English_UK, "en-GB");
		checkAlias(Language.English_US, "en-US", "en", "whatever");
	}

	private void checkAlias(Language lang, String... aliases) {
		for (String alias : aliases) {
			assertEquals(alias + " should stand for " + lang,
					Language.fromLanguageTagOrLocaleString(alias),
					lang);
		}
	}

	@Test
	public void localizedFunctionsShouldBeInnternalInXML() {
		AppDNoGui app = AlgebraTest.createApp();
		app.setLanguage(Locale.GERMANY);
		assertEquals("Midpoint(10,20)",
				GgbScript.localizedScript2Script(app, "Mittelpunkt(10,20)"));
		assertEquals("nroot(10,20)",
				GgbScript.localizedScript2Script(app, "NteWurzel(10,20)"));
		assertEquals("nroot[10,20]",
				GgbScript.localizedScript2Script(app, "NteWurzel[10,20]"));
	}
}
