package org.geogebra.common.gui;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Dummy implementation for desktop/mobile
 */
public final class AccessibilityManagerNoGui
		implements AccessibilityManagerInterface {

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
	public void setTabOverGeos() {
		// not used
	}

	@Override
	public void appendAltText(GeoText altText) {
		// not used
	}

	@Override
	public boolean isIndependentFromAltTexts(GeoNumeric geo) {
		return true;
	}

	@Override
	public void addAsAltTextDependency(GeoNumeric geo) {
		// not used
	}
}
