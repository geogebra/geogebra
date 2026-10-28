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

package org.geogebra.common.kernel.cas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.jspecify.annotations.NonNull;

/**
 * Loads the test cases from giacTests.js.
 */
public final class GiacTestFile {

	private static final String RESOURCE = "/giac/giacTests.js";

	private GiacTestFile() {
		// utility class
	}

	/**
	 * Loads giacTests.js from the classpath.
	 * @return test cases grouped by category, in file order
	 */
	public static @NonNull Map<String, List<GiacTestCase>> load() {
		InputStream stream = GiacTestFile.class.getResourceAsStream(RESOURCE);
		if (stream == null) {
			throw new IllegalStateException("Missing resource " + RESOURCE);
		}
		StringBuilder json = new StringBuilder();
		try (BufferedReader reader =
				new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().startsWith("//")) {
					json.append(line);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		JSONArray tests;
		try {
			tests = new JSONArray(json.substring(json.indexOf("[")));
		} catch (JSONException e) {
			throw new IllegalStateException(e);
		}
		return parse(tests);
	}

	/**
	 * @param tests test cases of giacTests.js
	 * @return test cases grouped by category, in file order
	 */
	private static @NonNull Map<String, List<GiacTestCase>> parse(@NonNull JSONArray tests) {
		Map<String, List<GiacTestCase>> categories = new LinkedHashMap<>();
		for (int i = 0; i < tests.length(); i++) {
			if (tests.isNull(i)) {
				continue; // trailing comma
			}
			JSONObject test = tests.optJSONObject(i);
			if (test == null || !test.has("cat")) {
				throw new IllegalStateException("Invalid test case: " + tests.opt(i));
			}
			List<String> expected = new ArrayList<>();
			if (test.has("round")) {
				expected.addAll(Arrays.asList(test.optString("round").split("\\|OR\\|")));
			}
			expected.addAll(Arrays.asList(test.optString("result").split("\\|OR\\|")));
			String rounding = test.has("rounding") ? test.optString("rounding") : null;
			categories
					.computeIfAbsent(test.optString("cat"), k -> new ArrayList<>())
					.add(new GiacTestCase(test.optString("cmd"), expected, rounding));
		}
		return categories;
	}
}
