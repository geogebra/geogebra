package org.geogebra.common.gui;

import org.geogebra.common.kernel.geos.GeoElement;

public final class AccessibilityManagerNoGui
		implements AccessibilityManagerInterface {
	@Override
	public void focusNext(Object source) {
		// only tab geos
	}

	@Override
	public void focusPrevious(Object source) {
		// only tab geos
	}

	@Override
	public void focusMenu() {
		// only tab geos
	}

	@Override
	public boolean focusInput(boolean force) {
		return false;
	}

	@Override
	public boolean isTabOverGeos() {
		return true;
	}

	@Override
	public boolean isCurrentTabExitGeos(boolean isShiftDown) {
		return false;
	}

	@Override
	public void setTabOverGeos(boolean b) {
		// always true anyway
	}

	@Override
	public void focusGeo(GeoElement geo) {
		// only called from AV
	}

	@Override
	public void setAnchor(Object anchor) {
		// not needed
	}

	@Override
	public Object getAnchor() {
		// TODO Auto-generated method stub
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
	public boolean handleTabExitGeos(boolean forward) {
		return false;
	}

	@Override
	public boolean leaveAnimationButton(boolean forward) {
		return false;
	}

	@Override
	public void setPlaySelectedIfVisible(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSpaceAction() {
		return null;
	}

	@Override
	public GeoElement getSelectedGeo() {
		// TODO Auto-generated method stub
		return null;
	}
}