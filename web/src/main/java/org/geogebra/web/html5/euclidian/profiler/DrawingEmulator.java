package org.geogebra.web.html5.euclidian.profiler;

import java.util.List;
import javax.annotation.Nullable;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.util.profiler.FpsProfiler;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.profiler.coords.Coordinate;
import org.geogebra.web.html5.euclidian.profiler.coords.CoordinatesParser;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.util.file.FileLoader;

import com.google.gwt.user.client.Timer;

public class DrawingEmulator {

	private class Drawer extends Timer {

		private Coordinate coordinate;

		private void setCoordinate(Coordinate coordinate) {
			this.coordinate = coordinate;
		}

		@Override
		public void run() {
			mouseTouchGestureController
					.onTouchMoveNow(
							newPointerEvent(coordinate), coordinate.getTime(), false);
		}
	}

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
		FpsProfiler fpsProfiler = mouseTouchGestureController.getApp().getFpsProfiler();
		startDrawing(fpsProfiler);
		doDraw();
		endDrawing(fpsProfiler);
	}

	private void startDrawing(FpsProfiler fpsProfiler) {
		fpsProfiler.notifyTouchStart();
		mouseTouchGestureController.onTouchStart(newPointerEvent(coordinates.get(0)));
	}

	private void doDraw() {
		Coordinate previousCoordinate = null;
		for (Coordinate coordinate : coordinates) {
			Drawer drawer = new Drawer();
			drawer.setCoordinate(coordinate);
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

	private void endDrawing(final FpsProfiler fpsProfiler) {
		new Timer() {
			@Override
			public void run() {
				mouseTouchGestureController.onTouchEnd();
				fpsProfiler.notifyTouchEnd();
				sleepIntervalSum = 0;
			}
		}.schedule(sleepIntervalSum);
	}

	private PointerEvent newPointerEvent(Coordinate coordinate) {
		return new PointerEvent(
				coordinate.getX(), coordinate.getY(),
				PointerEventType.TOUCH,
				mouseTouchGestureController);
	}
}
