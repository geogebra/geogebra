package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.util.debug.Log;

/**
 * Controller for Interval Plotter to handle zoom and moving the view.
 *
 * @author laszlo
 */
public class IntervalPlotController implements CoordSystemAnimationListener, SettingListener {

	private final IntervalPlotModel model;
	private EuclidianController euclidianController;
	private EuclidianSettings euclidianSettings;
	private FunctionSampler sampler;

	/**
	 * Constructor.
	 * @param model {@link IntervalPlotModel}
	 */
	public IntervalPlotController(IntervalPlotModel model) {
		this.model = model;
	}

	/**
	 * @param controller {@link EuclidianController}
	 */
	public void attachEuclidianController(EuclidianController controller) {
		euclidianController = controller;
		euclidianController.addZoomerAnimationListener(this, model.getGeoFunction());
		euclidianSettings = euclidianController.getView().getSettings();
		euclidianSettings.addListener(this);
	}

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		info.setXAxisZoom(false);
		if (IntervalPlotSettings.isUpdateOnZoomStopEnabled()) {
			model.updateAll();
		}
	}

	@Override
	public void onMoveStop() {
		if (IntervalPlotSettings.isUpdateOnMoveStopEnabled()) {
			model.updateAll();
		}
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		if (info.isXAxisZoom() || info.isCenterVew()) {
			return;
		}

		if (IntervalPlotSettings.isUpdateOnMoveEnabled()) {
			model.updateDomain();
		}
	}

	/**
	 * Remove controller as zoomer animation listener.
	 */
	public void detach() {
		euclidianController.removeZoomerAnimationListener(model.getGeoFunction());
		euclidianSettings.removeListener(this);
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		if (IntervalPlotSettings.isUpdateOnSettingsChangeEnabled()) {
			Log.debug("Settings changed");
			model.updateAll();
		}
	}
}