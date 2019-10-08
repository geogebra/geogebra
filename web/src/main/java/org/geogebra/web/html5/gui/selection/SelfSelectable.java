package org.geogebra.web.html5.gui.selection;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.selection.Selectable;

import com.google.gwt.dom.client.Element;

/**
 * Wraps a GWT element.
 */
class SelfSelectable implements Selectable {

	private Element element;

	/**
	 * @param element GWT element to be wrapped.
	 */
	SelfSelectable(Element element) {
		this.element = element;
	}

	@Override
	public boolean canSelectSelf() {
		return true;
	}

	@Override
	public void selectSelf() {
		element.focus();
	}

	@Override
	public GeoElement getGeoElement() {
		return null;
	}

	Element getGwtElement() {
		return element;
	}
}
