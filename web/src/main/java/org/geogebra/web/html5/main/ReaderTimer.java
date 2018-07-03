package org.geogebra.web.html5.main;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.ScreenReader;

import com.google.gwt.user.client.Timer;

class ReaderTimer extends Timer {

	/**
	 * 
	 */
	private final AppW appW;
	GeoNumeric geo;

	protected ReaderTimer(AppW appW) {
		this.appW = appW;
		// make protected
	}

	@Override
	public void run() {
		ScreenReader.readText(geo);
	}

	public void setGeo(GeoNumeric geo0) {
		geo = geo0;
	}

}