package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuActionCollection;

/**
 * Manu item collection for a GeoElement
 * 
 * @author Zbynek
 */
public class GeoElementMenuItemCollection
		extends MenuActionCollection<GeoElement> {

	/**
	 * Add "Add label" / "Remove label" to a menu
	 * 
	 */
	public void addLabelingActions() {
		addActions(new RemoveLabelAction(), new AddLabelAction());
	}

}
