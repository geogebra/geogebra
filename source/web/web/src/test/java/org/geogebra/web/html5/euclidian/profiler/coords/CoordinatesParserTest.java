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

package org.geogebra.web.html5.euclidian.profiler.coords;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.junit.Test;

public class CoordinatesParserTest {

	private static final String COORDS_JSON_STRING =
			"{coords:[{\"x\":0, \"y\":1, \"time\":2},\n{\"touchEnd\":1}]}";

	@Test
	public void parseCoordinates() throws JSONException {
		List<Coordinate> coordinates = CoordinatesParser.parseCoordinates(COORDS_JSON_STRING);
		Coordinate coordinate = coordinates.get(0);
		assertThat(coordinate.getX(), equalTo(0.0));
		assertThat(coordinate.getY(), equalTo(1.0));
		assertThat(coordinate.getTime(), equalTo(2L));
		assertThat(coordinate.isTouchEnd(), equalTo(true));
	}
}