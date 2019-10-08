package org.geogebra.web.html5.gui.selection;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.selection.Selectable;
import org.geogebra.common.main.selection.SelectionRegistry;

import com.google.gwt.dom.client.Element;

/**
 * Selection registry for GWT- and GeoElements.
 */
public class SelectionRegistryW implements SelectionRegistry {

	private Element lastSelectedGwtElement;
	private GeoElement lastSelectedGeoElement;

	@Override
	public void register(Selectable selected) {
		if (selected instanceof GeoSelectable) {
			register(selected.getGeoElement());
		} else if (selected instanceof SelfSelectable) {
			register(((SelfSelectable) selected).getGwtElement());
		}
	}

	@Override
	public void register(GeoElement selected) {
		lastSelectedGeoElement = selected;
		lastSelectedGwtElement = null;
	}

	/**
	 * Registers the selection.
	 * @param selected The selected element to be saved.
	 */
	public void register(Element selected) {
		lastSelectedGwtElement = selected;
		lastSelectedGeoElement = null;
	}

	@Override
	public Selectable getLastSelectedElement() {
		if (lastSelectedGeoElement != null) {
			return new GeoSelectable(lastSelectedGeoElement);
		} else {
			return new SelfSelectable(lastSelectedGwtElement);
		}
	}
}
