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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoSpotlight;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.GTimer;

/**
 * Spotlight related operations
 *
 * @author Laszlo
 */
public class SpotlightController {

	public static final int BOX_DISAPPEAR_DELAY = 3600;
	private final Construction cons;
	private final GTimer disappearBoxTimer;
	private final App app;
	private final SelectionManager selectionManager;
	private GeoSpotlight spotlight = null;

	/**
	 * Constructor
	 * @param app the application
	 */
	public SpotlightController(App app) {
		cons = app.getKernel().getConstruction();
		this.app = app;
		disappearBoxTimer = app.newTimer(this::disappearBoundingBox, BOX_DISAPPEAR_DELAY);
		selectionManager = app.getSelectionManager();
	}

	/**
	 * Turns spotlight on.
	 */
	public void turnOn() {
		app.setMode(EuclidianConstants.MODE_SELECT_MOW);
		spotlight = new GeoSpotlight(cons);
		selectionManager.clearSelectedGeos();
		selectionManager.addSelectedGeo(spotlight);
		disappearBox();
		spotlight.updateRepaint();
	}

	/**
	 * Makes bounding box disappear in some time.
	 */
	public void disappearBox() {
		if (app.getSelectionManager().containsSelectedGeo(spotlight())) {
			disappearBoxTimer.start();
		}
	}

	/**
	 * Turns spotlight off.
	 */
	public void turnOff() {
		if (spotlight != null && canBeRemoved()) {
			disappearBoxTimer.stop();
			spotlight.remove();
		}
	}

	/**
	 *
	 * @return spotlight geo
	 */
	GeoSpotlight spotlight() {
		return spotlight;
	}

	private boolean canBeRemoved() {
		EuclidianView ev = app.getActiveEuclidianView();
		return !ev.getHits().contains(spotlight())
				&& ev.getHitHandler() == EuclidianBoundingBoxHandler.UNDEFINED;
	}

	/**
	 * Prevents bounding box disappearing after a given time.
	 */
	public void keepBox() {
		disappearBoxTimer.stop();
	}

	private void disappearBoundingBox() {
		selectionManager.clearSelectedGeos();
		app.getActiveEuclidianView().setBoundingBox(null);
		app.getActiveEuclidianView().setCursor(EuclidianCursor.DRAG);
	}

	/**
	 * clears spotlight
	 */
	public void clear() {
		spotlight = null;
	}
}
