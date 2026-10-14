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

package org.geogebra.common.euclidian;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.jspecify.annotations.Nullable;

/**
 * Creates, updates and removes a point that can be moved along a selected path.
 */
class PathTracker {
	static int DELAY = 150;
	private final EuclidianController controller;
	private GeoFunctionable trackedGeoFunction;
	private GTimer functionTrackerTimer;
	// Keep the callback alive while iOS timers hold listeners weakly.
	private @Nullable GTimerListener functionTrackerListener;
	private GeoPoint trackingPoint;

	PathTracker(EuclidianController controller) {
		this.controller = controller;
	}

	void update(int x, int y) {
		EuclidianView view = controller.getView();
		double rwx = view.toRealWorldCoordX(x);
		double rwy = view.toRealWorldCoordY(y);
		if (trackingPoint == null) {
			trackingPoint = new GeoPoint(view.getKernel().getConstruction());
			trackingPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE);
			trackingPoint.setPointSize(3);
		}
		trackingPoint.setCoords(rwx, rwy, 1);
		trackingPoint.setObjColor(trackedGeoFunction.getObjectColor());
		if (trackedGeoFunction.unwrapSymbolic() instanceof Path path) {
			path.pointChanged(trackingPoint);
			trackingPoint.setEuclidianVisible(true);
			trackingPoint.updateCoords();
		} else {
			trackingPoint.setEuclidianVisible(false);
		}
		view.updateFunctionTracker(trackingPoint);
		view.getEuclidianController().showSpecialPointPopup(List.of(trackingPoint));
	}

	void reset() {
		if (trackedGeoFunction != null) {
			trackedGeoFunction = null;
			controller.hideSpecialPointPopup();
			controller.getView().resetFunctionTracker(trackingPoint);
			trackingPoint = null;
		}
		if (functionTrackerTimer != null) {
			functionTrackerTimer.stop();
			functionTrackerTimer = null;
		}
		functionTrackerListener = null;
	}

	void schedule(GeoFunctionable function) {
		functionTrackerListener = () -> {
			trackedGeoFunction = function;
			GPoint pointerLocation = controller.getMouseLoc();
			update(pointerLocation.x, pointerLocation.y);
			controller.moveMode = MoveMode.FUNCTION_TRACKER;
			functionTrackerListener = null;
		};
		functionTrackerTimer = UtilFactory.getPrototype().newTimer(functionTrackerListener, DELAY);
		functionTrackerTimer.start();
	}
}
