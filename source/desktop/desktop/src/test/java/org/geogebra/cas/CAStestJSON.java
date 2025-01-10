package org.geogebra.cas;

import static org.junit.Assume.assumeFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import org.geogebra.common.kernel.cas.CasTestJsonCommon;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

public class CAStestJSON extends CasTestJsonCommon {

	/**
	 * Create app and algebra processor.
	 */
	@BeforeClass
	public static void setupCas() {
		app = new AppDNoGui(new LocalizationD(3), false);
		// Set language to something else than English to test automatic
		// translation.
		((AppDNoGui) app).setLanguage(Locale.GERMANY);
		// app.fillCasCommandDict();

		kernel = app.getKernel();
		cas = kernel.getGeoGebraCAS();
		// Setting the general timeout to 13 seconds. Feel free to change this.
		kernel.getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(13000);
		String json = "";
		try {
			Log.debug("CAS: loading testcases");
			json = readJsonFileAsString(
			);
			Log.debug("CAS: testcases parsed");
			addTestcases(json);
		} catch (JSONException | IOException e) {
			handleParsingError(e, json);
		}
		checkMissingCategories();
	}

	@Override
	protected void testCatNoClang(String category) {
		if (!AppD.MAC_OS) {
			testCat(category);
		} else {
			testcases.remove(category);
			assumeFalse(true); // mark test as skipped
		}
	}

	/**
	 * Check that all categories were tested.
	 */
	@AfterClass
	public static void checkAllCatsTested() {
		if (missing != null) {
			Assert.fail("Missing tests for:" + missing);
		}
		StringBuilder sb = new StringBuilder();
		for (String cat : testcases.keySet()) {
			sb.append(cat);
			sb.append(',');
		}
		Assert.assertEquals("", sb.toString());
	}

	private static String readJsonFileAsString() throws IOException {
		return skipComments(Files.readAllLines(Paths.get(
				"../common/src/main/resources/giac/giacTests.js")));
	}

}
