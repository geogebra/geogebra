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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;

/**
 * Parses the Coordinate objects from a json string.
 */
public final class CoordinatesParser {

	private static final String COORDS = "coords";
	private static final String TIME = "time";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String TOUCH_END = "touchEnd";

	private static CoordinatesParser instance;

	private CoordinatesParser() {
	}

	private static CoordinatesParser getInstance() {
		if (instance == null) {
			instance = new CoordinatesParser();
		}
		return instance;
	}

	/**
	 * Parses the jsonString into a list of Coordinate objects.
	 *
	 * @param jsonString Json string
	 * @return List of Coordinate objects
	 * @throws JSONException This is thrown when the jsonString parameter
	 *                       is not a correct json string.
	 */
	public static List<Coordinate> parseCoordinates(String jsonString) throws JSONException {
		CoordinatesParser coordinatesParserInstance = getInstance();
		JSONObject rootJsonObject = new JSONObject(jsonString);
		JSONArray coordsJsonArray = rootJsonObject.getJSONArray(COORDS);
		List<Coordinate> coordinates = new ArrayList<>();
		Coordinate prevCoordinate = null;
		for (int i = 0; i < coordsJsonArray.length(); i++) {
			Coordinate coordinate =
					coordinatesParserInstance
							.parseCoordinateOrTouchEnd(coordsJsonArray.getJSONObject(i));
			if (coordinate != null) {
				coordinates.add(coordinate);
				prevCoordinate = coordinate;
			} else {
				Objects.requireNonNull(prevCoordinate).setTouchEnd(true);
			}
		}
		return coordinates;
	}

	private Coordinate parseCoordinateOrTouchEnd(JSONObject jsonObject) throws JSONException {
		if (jsonObject.has(TOUCH_END)) {
			return null;
		} else {
			return parseCoordinate(jsonObject);
		}
	}

	private Coordinate parseCoordinate(JSONObject coordJsonObject) throws JSONException {
		Coordinate coordinate = new Coordinate();
		coordinate.setTime(coordJsonObject.getLong(TIME));
		coordinate.setX(coordJsonObject.getDouble(X));
		coordinate.setY(coordJsonObject.getDouble(Y));
		return coordinate;
	}
}
