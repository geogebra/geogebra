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

	public GeoElement getAltGeo() {
		return altTexts.getAltGeo(viewIndex);
	}
}