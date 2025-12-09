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

package org.geogebra.cas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
			json = readJsonFileAsString();
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
		assertNull("Missing tests for:" + missing, missing);
		StringBuilder sb = new StringBuilder();
		for (String cat : testcases.keySet()) {
			sb.append(cat);
			sb.append(',');
		}
		assertEquals("", sb.toString());
	}

	private static String readJsonFileAsString() throws IOException {
		return skipComments(Files.readAllLines(Paths.get(
				"../../shared/common/src/main/resources/giac/giacTests.js")));
	}

}
