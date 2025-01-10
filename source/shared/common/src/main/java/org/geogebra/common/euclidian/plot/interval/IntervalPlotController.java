package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.SettingListener;

/**
 * Controller for Interval Plotter to handle zoom and moving the view.
 *
 * @author laszlo
 */
public class IntervalPlotController implements CoordSystemAnimationListener, SettingListener {

	private final IntervalFunctionModel model;
	private final GeoFunction function;
	private EuclidianController euclidianController;
	private EuclidianSettings euclidianSettings;

	/**
	 * Constructor.
	 * @param model {@link IntervalFunctionModelImpl}
	 * @param function {@link GeoFunction}
	 */
	public IntervalPlotController(IntervalFunctionModel model, GeoFunction function) {
		this.model = model;
		this.function = function;
	}

	/**
	 * @param view view for the drawable
	 */
	public void attachEuclidianView(EuclidianView view) {
		euclidianController = view.getEuclidianController();
		euclidianController.addZoomerAnimationListener(this, function);
		euclidianSettings = view.getSettings();
		if (euclidianSettings != null) {
			euclidianSettings.addListener(this);
		}
	}

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		info.setXAxisZoom(false);
		if (IntervalPlotSettings.isUpdateOnZoomStopEnabled()) {
			model.resample();
		}
	}

	@Override
	public void onMoveStop() {
		if (IntervalPlotSettings.isUpdateOnMoveStopEnabled()) {
			model.resample();
		}
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		if (info.isXAxisZoom() || info.isCenterView()) {
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
		euclidianController.removeZoomerAnimationListener(function);
		if (euclidianSettings != null) {
			euclidianSettings.removeListener(this);
		}
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		if (IntervalPlotSettings.isUpdateOnSettingsChangeEnabled()) {
			model.resample();
		}
	}
}