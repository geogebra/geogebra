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
	private SelectionManager selectionManager;
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
