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
 
package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.junit.Test;

public class NumberFormatTest {

	@Test
	public void testRounding() throws IOException, JSONException {
		String path = "../../shared/common/src/main/resources/testData/rounding.json";
		String content = new String(Files.readAllBytes(Paths.get(path)));
		JSONArray cases = new JSONArray(new JSONTokener(content));
		AppCommon app = AppCommonFactory.create();
		for (int idx = 0; idx < cases.length(); idx++) {
			JSONObject testCase = cases.getJSONObject(idx);
			AlgebraProcessor processor = app.getKernel().getAlgebraProcessor();
			String in = testCase.getString("in");
			GeoElementND geo = processor.processAlgebraCommand(in, false)[0];
			JSONObject out = testCase.getJSONObject("out");
			for (String key: out.keySet()) {
				app.setRounding(key);
				assertEquals(in + " rounded incorrectly at " + key,
						out.get(key), geo.toValueString(StringTemplate.defaultTemplate));
			}
		}
	}
}
