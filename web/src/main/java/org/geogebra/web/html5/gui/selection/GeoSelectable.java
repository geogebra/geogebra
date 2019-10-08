package org.geogebra.web.html5.gui.selection;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.selection.Selectable;

/**
 * Wraps a GeoElement.
 */
class GeoSelectable implements Selectable {

	private GeoElement element;

	/**
	 * @param element GeoElement to be wrapped.
	 */
	GeoSelectable(GeoElement element) {
		this.element = element;
	}

	@Override
	public boolean canSelectSelf() {
		return false;
	}

	@Override
	public void selectSelf() {
		// no-op
	}

	@Override
	public GeoElement getGeoElement() {
		return element;
	}
}
