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

import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;

public class AltGeoTabber implements MayHaveFocus {
	private final ScreenReaderAdapter screenReader;
	private boolean focus = false;
	private int viewIndex = 0;
	private final ViewAltTexts altTexts;

	/**
	 * @param app the application
	 */
	public AltGeoTabber(App app, ViewAltTexts altTexts) {
		screenReader = app.getActiveEuclidianView().getScreenReader();
		this.altTexts = altTexts;
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		altTexts.updateVisibleViews();
		viewIndex = reverse ? altTexts.viewCount() - 1 : 0;
		if (readNextView())  {
			return focus;
		}
		return false;
	}

	private boolean readNextView() {
		if (viewIndex < altTexts.viewCount()) {
			readAltTextForView();
			return true;
		}
		return false;
	}

	private void readAltTextForView() {
		GeoElement altGeo = altTexts.getAltGeo(viewIndex);
		if (altGeo != null) {
			readText(screenReader, altGeo);
			focus = true;
		}
	}

	private void readText(ScreenReaderAdapter screenReader, GeoElement altGeo) {
		if (!altGeo.isGeoText()) {
			return;
		}
		screenReader.readText(((GeoText) altGeo).getAuralText());
	}

	@Override
	public boolean hasFocus() {
		return focus;
	}

	@Override
	public boolean focusNext() {
		viewIndex++;
		focus = readNextView();
		return focus;
	}

	@Override
	public boolean focusPrevious() {
		viewIndex--;
		focus = readPreviousView();
		return focus;
	}

	private boolean readPreviousView() {
		if (viewIndex >= 0) {
			readAltTextForView();
			return true;
		}
		return false;
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.ALT_GEOTEXT;
	}

	@Override
	public AccessibilityGroup.ViewControlId getViewControlId() {
		return AccessibilityGroup.ViewControlId.ALT_GEO;
	}

}