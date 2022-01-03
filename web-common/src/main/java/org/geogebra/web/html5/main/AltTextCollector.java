package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.accessibility.ViewAltTexts;

public class AltTextCollector {
	private final Localization loc;
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
		timer = new AltTextTimer(app.getActiveEuclidianView().getScreenReader());
		loc = app.getLocalization();
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
		timer.feed(altText.getAuralText());
	}

	/**
	 *
	 * @param geo to check
	 * @return if this geo is independent for all altText of views or not.
	 */
	public boolean isIndependent(GeoNumeric geo) {
		return views.isIndependent(geo);
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

	public List<GeoText> getDependentAltTexts(GeoNumeric geo) {
		return views.getDependentAltTexts(geo);
	}

	/**
	 *  Reads all list of views' altTexts that are dependent on the given geo.
	 * @param geo to depend on.
	 * @param altTexts list of dependent altTexts.
	 */
	public void readDependentAltTexts(GeoNumeric geo, List<GeoText> altTexts) {
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		sb.append(geo.getAuralCurrentValue());
		altTexts.forEach(geoText -> {
			geoText.addAuralContent(loc, sb);
			sb.endSentence();
		});
		timer.feed(sb.toString());
	}
}
