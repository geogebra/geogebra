package org.geogebra.common.gui;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * Accessibility manager for app with no UI (simple applets)
 */
public final class AccessibilityManagerNoGui
		implements AccessibilityManagerInterface {

	@Override
	public void focusNext(AccessibilityGroup group, int viewID) {
		// only tab geos
	}

	@Override
	public void focusPrevious(AccessibilityGroup group, int viewID) {
		// only tab geos
	}

	@Override
	public void focusFirstElement() {
		// only tab geos
	}

	@Override
	public boolean focusInput(boolean force) {
		return false;
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
	public boolean tabEuclidianControl(boolean forward) {
		return false;
	}

	@Override
	public void setPlaySelectedIfVisible(boolean b, int viewID) {
		// TODO Auto-generated method stub
	}

	/**
	 * @param app
	 *            app
	 * @return single selected geo
	 */
	public static GeoElement getSelectedGeo(App app) {
		return app.getSelectionManager().getSelectedGeos().size() == 1
				? app.getSelectionManager().getSelectedGeos().get(0) : null;
	}

	@Override
	public void sliderChange(double step, SliderInput input) {
		// no slider
	}

	@Override
	public boolean onSelectFirstGeo(boolean forward) {
		return false;
	}

	@Override
	public boolean onSelectLastGeo(boolean forward) {
		return false;
	}

	@Override
	public void onEmptyConstuction(boolean forward) {
		// not used
	}

	@Override
	public String getAction(GeoElement geo) {
		return "";
	}
}
