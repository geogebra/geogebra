package org.geogebra.common.gui;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;

/**
 * Dummy implementation for desktop/mobile
 */
public final class AccessibilityManagerNoGui
		implements AccessibilityManagerInterface {

	private final App app;
	private final AltTextTimer timer;

	/**
	 * @param app application
	 */
	public AccessibilityManagerNoGui(App app) {
		this.app = app;
		timer = new AltTextTimer(app.getActiveEuclidianView().getScreenReader(),
				app.getLocalization());
	}

	@Override
	public boolean focusNext() {
		// only tab geos
		return false;
	}

	@Override
	public boolean focusPrevious() {
		// only tab geos
		return false;
	}

	@Override
	public void focusFirstElement() {
		// only tab geos
	}

	@Override
	public boolean focusInput(boolean force, boolean forceFade) {
		return false;
	}

	@Override
	public void focusGeo(GeoElement geo) {
		// only called from AV
	}

	@Override
	public void setAnchor(MayHaveFocus anchor) {
		// not needed
	}

	@Override
	public MayHaveFocus getAnchor() {
		return null;
	}

	@Override
	public void focusAnchor() {
		// not needed
	}

	@Override
	public void cancelAnchor() {
		// not needed
	}

	@Override
	public void focusAnchorOrMenu() {
		// not needed
	}

	@Override
	public void register(MayHaveFocus focusable) {
		// nothing to do
	}

	@Override
	public void unregister(MayHaveFocus focusable) {
		// not needed
	}

	@Override
	public void setTabOverGeos() {
		app.getSelectionManager().resetKeyboardSelection();
	}

	@Override
	public void appendAltText(GeoText altText) {
		// not used
	}

	@Override
	public void cancelReadCollectedAltTexts() {
		// not used
	}

	@Override
	public void readSliderUpdate(GeoNumeric geo) {
		if (!app.getKernel().getConstruction().isFileLoading()
				&& !geo.isAnimating()) {
			timer.feed(geo);
		}
	}

	@Override
	public void preloadAltText(GeoText geoText) {
		// not used
	}
}
