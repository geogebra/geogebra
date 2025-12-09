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

package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;

/**
 * Class to provide the access of altTexts for the visible views
 * @author laszlo
 */
public class ViewAltTexts {
	private static final List<Integer> availableViews = Arrays.asList(
			App.VIEW_EUCLIDIAN,
			App.VIEW_EUCLIDIAN2,
			App.VIEW_EUCLIDIAN3D);
	private static final HashMap<Integer, String>
			altTextsPerView = new HashMap<>();
	private List<Integer> visibleViews;

	static {
		altTextsPerView.put(App.VIEW_EUCLIDIAN, "altText");
		altTextsPerView.put(App.VIEW_EUCLIDIAN2, "altText2");
		altTextsPerView.put(App.VIEW_EUCLIDIAN3D, "altText3D");
	}

	private final Kernel kernel;

	private final App app;

	/**
	 * @param app the application
	 */
	public ViewAltTexts(App app) {
		this.app = app;
		kernel = app.getKernel();
		visibleViews = new ArrayList<>();
	}

	int viewCount() {
		return visibleViews.size();
	}

	/**
	 * @return the number of views that have altText and visible;
	 */
	public int activeAltTextCount() {
		updateVisibleViews();
		return visibleViews.size();
	}

	void updateVisibleViews() {
		visibleViews = availableViews.stream().filter(app::showView).collect(Collectors.toList());
	}

	/**
	 * @param viewIndex the index of the euclidian view.
	 * @return the geo element containing the alt text for the view
	 * specified by viewIndex.
	 */
	public GeoElement getAltGeo(int viewIndex) {
		GeoElement geoElement = kernel.lookupLabel(get(viewIndex));
		return (geoElement == null || geoElement.isEuclidianVisible())
				? null
				: geoElement;
	}

	private String get(int index) {
		return altTextsPerView.get(visibleViews.get(index));
	}

	/**
	 * @param altText to check
	 * @return if there is a view for the altText
	 */
	public boolean isValid(GeoText altText) {
		updateVisibleViews();
		String label = altText.getLabelSimple();
		for (Integer viewId : visibleViews) {
			if (altTextsPerView.get(viewId).equals(label)) {
				return true;
			}
		}

		return false;
	}
}