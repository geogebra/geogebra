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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.TreeSet;

import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.debug.Log;
import org.junit.Test;

public class ArticleTest {
	@Test
	public void documentedParameters() {
		Method[] mtds = AppletParameters.class.getMethods();
		TreeSet<String> documented = new TreeSet<>();
		for (Method mtd : mtds) {
			if (mtd.getName().contains("Data")) {
				documented.add(uncapitalize(
						mtd.getName().replace("getDataParam", "")));
				System.out.println("\"" + uncapitalize(
								mtd.getName().replace("getDataParam", ""))
						+ "\":\"boolean\",");
			}
		}
		try {
			URL u = new URL(
					"https://wiki.geogebra.org/s/en/api.php?action=query&prop=revisions&titles=Reference:GeoGebra_App_Parameters&rvprop=timestamp%7Cuser%7Ccomment%7Ccontent&format=json");
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setConnectTimeout(10000);
			huc.setRequestMethod("GET");
			huc.connect();
			String answer = "", s;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					huc.getInputStream(), StandardCharsets.UTF_8));
			answer = in.readLine(); // the last line will never get a "\n" on
			// its end
			while ((s = in.readLine()) != null) {
				if (!("".equals(answer))) {
					// ignore them
					answer += "\n";
				}
				answer += s;
			}
			JSONTokener tokener = new JSONTokener(answer);
			JSONObject response = new JSONObject(tokener);
			String wiki =
					response.getJSONObject("query").getJSONObject("pages")
							.getJSONObject("51").getJSONArray("revisions")
							.getJSONObject(0)
							.getString("*");
			String[] rows = wiki.split("\\|-");
			for (String row : rows) {
				String[] cells = row.split("\\n");

				documented.remove(cells[1].replace("|", ""));
			}
			for (String row : documented) {
				System.out.println(row);
			}
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	private static String uncapitalize(String capitalCase) {
		return (capitalCase.charAt(0) + "").toLowerCase(Locale.ROOT)
				+ capitalCase.substring(1, capitalCase.length());
	}
}
