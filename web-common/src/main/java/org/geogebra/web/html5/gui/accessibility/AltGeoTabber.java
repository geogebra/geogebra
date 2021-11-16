package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

public class AltGeoTabber implements MayHaveFocus {
	private final EuclidianView view;
	private static final String ALT_TEXT_ID = "altText";
	private final Kernel kernel;
	private GeoElement altGeo;
	private boolean focus = false;

	/**
	 * @param view used to determine which altText object to read
	 */
	public AltGeoTabber(EuclidianView view) {
		this.view = view;
		kernel = view.getKernel();
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		altGeo = getAltGeo();
		if (hasInvisibleAltGeo()) {
			GeoText altText = (GeoText) altGeo;
			view.getScreenReader().readText(altText.getText().getTextString());
			focus = true;
			return true;
		}
		return false;
	}

	private boolean hasInvisibleAltGeo() {
		return altGeo != null && !altGeo.isEuclidianVisible();
	}

	@Override
	public boolean hasFocus() {
		return focus;
	}

	@Override
	public boolean focusNext() {
		focus = false;
		return false;
	}

	@Override
	public boolean focusPrevious() {
		focus = false;
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

	/**
	 * @return the element with the name ("altText" + viewNumber), or "altText",
	 * if it exists
	 */
	public GeoElement getAltGeo() {
		int viewNo = view.getEuclidianViewNo();
		GeoElement altGeoForView = kernel.lookupLabel(ALT_TEXT_ID + viewNo);
		return altGeoForView == null
				? kernel.lookupLabel(ALT_TEXT_ID)
				: altGeoForView;
	}
}
