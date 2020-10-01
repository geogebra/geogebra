package org.geogebra.web.html5.main;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.ScreenReader;
import org.gwtproject.timer.client.Timer;

class ReaderTimer extends Timer {

	/**
	 * Slider
	 */
	GeoNumeric geo;

	protected ReaderTimer() {
		// make protected
	}

	@Override
	public void run() {
		ScreenReader.readSliderValue(geo);
	}

	/**
	 * @param slider
	 *            slider
	 */
	public void setGeo(GeoNumeric slider) {
		geo = slider;
	}

}