package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.accessibility.ViewAltTexts;

public class AltTextCollector {
	private final AltTextTimer timer;
	private final ViewAltTexts views;
	private final List<GeoNumeric> dependencies;

	/**
	 *
	 * @param app the application
	 * @param views views' altTexts
	 */
	public AltTextCollector(App app, ViewAltTexts views) {
		this.views = views;
		timer = new AltTextTimer(app.getActiveEuclidianView().getScreenReader(),
				app.getLocalization());
		dependencies = new ArrayList<>();
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
		timer.feed(altText.getAuralText(), altText);
	}

	/**
	 * Add a GeoNumeric dependent to views' altText
	 * @param geo to add
	 */
	public void addDependency(GeoNumeric geo) {
		if (!dependencies.contains(geo)) {
			dependencies.add(geo);
		}
	}

	/**
	 * Adds slider to the queue of objects to be read
	 * @param geo changed slider
	 */
	public void readSliderUpdate(GeoNumeric geo) {
		timer.feed(geo.getAuralCurrentValue(), geo);
	}

	/**
	 * Stops reading the collected text.
	 */
	public void cancel() {
		timer.cancel();
	}
}
