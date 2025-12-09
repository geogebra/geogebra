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

package org.geogebra.web.html5.euclidian.profiler.drawer;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.euclidian.profiler.coords.Coordinate;
import org.geogebra.web.html5.euclidian.profiler.coords.CoordinatesParser;
import org.geogebra.web.html5.util.file.FileLoader;
import org.gwtproject.timer.client.Timer;

/**
 * Autonomously draws onto the canvas from a file. The name of the file is specified
 * by the FILE_NAME field.
 */
public class DrawingEmulator {

	private static final String FILE_NAME = "coords.json";

	private MouseTouchGestureControllerW mouseTouchGestureController;
	private List<Coordinate> coordinates;
	private int sleepIntervalSum;

	public DrawingEmulator(MouseTouchGestureControllerW mouseTouchGestureController) {
		this.mouseTouchGestureController = mouseTouchGestureController;
	}

	/**
	 * Autonomously draws onto the canvas from a file. The name of the file is specified
	 * by the FILE_NAME field.
	 */
	public void draw() {
		if (coordinates == null) {
			initCoordinatesAndDraw();
		} else {
			drawWithFpsProfiling();
		}
	}

	private void initCoordinatesAndDraw() {
		FileLoader.loadFile(FILE_NAME, getFileLoaderCallback());
	}

	private FileLoader.Callback getFileLoaderCallback() {
		return fileContent -> {
			try {
				coordinates = CoordinatesParser.parseCoordinates(fileContent);
				drawWithFpsProfiling();
			} catch (JSONException e) {
				Log.debug(e);
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
				drawer.initiateDrawingWithTouchStart();
			}
			drawer.schedule(getSleepInterval(previousCoordinate, coordinate));
			previousCoordinate = coordinate;
		}
	}

	private int getSleepInterval(
			@CheckForNull Coordinate previousCoordinate, Coordinate actualCoordinate) {
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
				GeoGebraProfiler.printDragMeasurementData();
			}
		}.schedule(sleepIntervalSum);
	}
}
