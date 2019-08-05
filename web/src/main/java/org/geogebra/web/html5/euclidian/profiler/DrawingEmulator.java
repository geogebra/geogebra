package org.geogebra.web.html5.euclidian.profiler;

import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.profiler.coords.Coordinate;
import org.geogebra.web.html5.euclidian.profiler.coords.CoordinatesParser;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.util.file.FileLoader;

public class DrawingEmulator {

	private static final String FILE_URL = "coords.json";

	private MouseTouchGestureControllerW mouseTouchGestureController;
	private List<Coordinate> coordinates;

	public DrawingEmulator(MouseTouchGestureControllerW mouseTouchGestureController) {
		this.mouseTouchGestureController = mouseTouchGestureController;
	}

	public void draw() {
		if (coordinates == null) {
			initCoordinatesAndDraw();
		} else {
			doDraw();
		}
	}

	private void initCoordinatesAndDraw() {
		FileLoader.loadFile(FILE_URL, getFileLoaderCallback());
	}

	private FileLoader.Callback getFileLoaderCallback() {
		return new FileLoader.Callback() {
			@Override
			public void onLoad(String fileContent) {
				try {
					coordinates = CoordinatesParser.parseCoordinates(fileContent);
					doDraw();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	}

	private void doDraw() {
		mouseTouchGestureController.onTouchStart(newPointerEvent(coordinates.get(0)));
		for (Coordinate coordinate : coordinates) {
			mouseTouchGestureController
					.onTouchMoveNow(
							newPointerEvent(coordinate), coordinate.getTime(), false);
		}
		mouseTouchGestureController.onTouchEnd();
	}

	private PointerEvent newPointerEvent(Coordinate coordinate) {
		return new PointerEvent(
				coordinate.getX(), coordinate.getY(),
				PointerEventType.TOUCH,
				mouseTouchGestureController);
	}
}
