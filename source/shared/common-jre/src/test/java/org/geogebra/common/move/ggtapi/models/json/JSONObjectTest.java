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

package org.geogebra.common.move.ggtapi.models.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

public class JSONObjectTest {

	private static final String TEST_DATA = "{ \"coords\": [{\"x\":122.0001, \"y\":148}]}";

	@Test
	public void getString() throws JSONException {
		JSONObject jsonObject = new JSONObject(TEST_DATA);
		JSONArray coords = jsonObject.getJSONArray("coords");
		JSONObject firstCoord = coords.getJSONObject(0);
		double firstCoordY = firstCoord.getDouble("y");
		assertThat(firstCoordY, equalTo(148.0));
	}
}
