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
	private final List<String> lines;
	private final Localization loc;
	private final AltTextTimer timer;
	private final ViewAltTexts views;
	private final List<GeoNumeric> dependencies;

	/**
	 * @param app application
	 * @param views view alt texts
	 */
	public AltTextCollector(App app, ViewAltTexts views) {
		this.views = views;
		timer = new AltTextTimer(app.getActiveEuclidianView().getScreenReader());
		loc = app.getLocalization();
		lines = new ArrayList<>();
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

		lines.add(altText.getAuralText());
		if (isLastAltText()) {
			timer.read(concatLines());
			lines.clear();
		}
	}

	private ScreenReaderBuilder concatLines() {
		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		appendDependencies(sb);
		for (String line: lines) {
			sb.append(line);
			sb.endSentence();
		}
		dependencies.clear();
		return sb;
	}

	private void appendDependencies(ScreenReaderBuilder sb) {
		for (GeoNumeric numeric: dependencies) {
			sb.append(numeric.getAuralCurrentValue());
			sb.endSentence();
		}
	}

	private boolean isLastAltText() {
		return lines.size() == views.activeAltTextCount();
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
}
