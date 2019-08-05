package org.geogebra.web.html5.euclidian.profiler.coords;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;

public class CoordinatesParser {

	private static final String COORDS = "coords";
	private static final String TIME = "time";
	private static final String X = "x";
	private static final String Y = "y";

	private static CoordinatesParser instance;

	private CoordinatesParser() {
	}

	private static CoordinatesParser getInstance() {
		if (instance == null) {
			instance = new CoordinatesParser();
		}
		return instance;
	}

	public static List<Coordinate> parseCoordinates(String jsonString) throws JSONException {
		CoordinatesParser coordinatesParserInstance = getInstance();
		JSONObject rootJsonObject = new JSONObject(jsonString);
		JSONArray coordsJsonArray = rootJsonObject.getJSONArray(COORDS);
		List<Coordinate> coordinates = new ArrayList<>();
		for (int i = 0; i < coordsJsonArray.length(); i++) {
			Coordinate coordinate =
					coordinatesParserInstance.parseCoordinate(coordsJsonArray.getJSONObject(i));
			coordinates.add(coordinate);
		}
		return coordinates;
	}

	private Coordinate parseCoordinate(JSONObject coordJsonObject) throws JSONException {
		Coordinate coordinate = new Coordinate();
		coordinate.setTime(coordJsonObject.getLong(TIME));
		coordinate.setX(coordJsonObject.getDouble(X));
		coordinate.setY(coordJsonObject.getDouble(Y));
		return coordinate;
	}
}
