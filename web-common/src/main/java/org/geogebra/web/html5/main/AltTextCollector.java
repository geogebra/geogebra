package org.geogebra.web.html5.main;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.accessibility.ViewAltTexts;

public class AltTextCollector {
	private final AltTextTimer timer;
	private final ViewAltTexts views;

	/**
	 *
	 * @param app the application
	 * @param views views' altTexts
	 */
	public AltTextCollector(App app, ViewAltTexts views) {
		this.views = views;
		timer = new AltTextTimer(app.getActiveEuclidianView().getScreenReader(),
				app.getLocalization());
	}

	/**
	 * Adds altText to read it once with all of other views' .
	 *
	 * @param altText to add.
	 */
	public void add(GeoText altText) {
		if (!views.isValid(altText)) {
			return;
		}
		timer.feed(altText);
	}

	/**
	 * Adds slider to the queue of objects to be read
	 * @param geo changed slider
	 */
	public void readSliderUpdate(GeoNumeric geo) {
		timer.feed(geo);
	}

	/**
	 * Stops reading the collected text.
	 */
	public void cancel() {
		timer.cancel();
	}
}
