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
