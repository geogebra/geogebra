package org.geogebra.web.html5.euclidian.profiler.drawer;

import java.util.List;
import javax.annotation.Nullable;

import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.profiler.coords.Coordinate;
import org.geogebra.web.html5.euclidian.profiler.coords.CoordinatesParser;
import org.geogebra.web.html5.util.file.FileLoader;

import com.google.gwt.user.client.Timer;

public class DrawingEmulator {

	private static final String FILE_URL = "coords.json";

	private MouseTouchGestureControllerW mouseTouchGestureController;
	private List<Coordinate> coordinates;
	private int sleepIntervalSum;

	public DrawingEmulator(MouseTouchGestureControllerW mouseTouchGestureController) {
		this.mouseTouchGestureController = mouseTouchGestureController;
	}

	public void draw() {
		if (coordinates == null) {
			initCoordinatesAndDraw();
		} else {
			drawWithFpsProfiling();
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
					drawWithFpsProfiling();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	}

	private void drawWithFpsProfiling() {
		doDraw();
		endDrawing();
	}

	private void doDraw() {
		Coordinate previousCoordinate = null;
		for (Coordinate coordinate : coordinates) {
			Drawer drawer = new Drawer(mouseTouchGestureController, coordinate);
			if (previousCoordinate == null || previousCoordinate.isTouchEnd()) {
				drawer.setShouldStartTouch(true);
			}
			drawer.schedule(getSleepInterval(previousCoordinate, coordinate));
			previousCoordinate = coordinate;
		}
	}

	private int getSleepInterval(
			@Nullable Coordinate previousCoordinate, Coordinate actualCoordinate) {
		if (previousCoordinate == null) {
			return 0;
		} else {
			int interval = (int) (actualCoordinate.getTime() - previousCoordinate.getTime());
			sleepIntervalSum += interval;
			return sleepIntervalSum;
		}
	}

	private void endDrawing() {
		new Timer() {
			@Override
			public void run() {
				sleepIntervalSum = 0;
			}
		}.schedule(sleepIntervalSum);
	}
}
