package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.MenuItemCollection;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.AddLabelItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.RemoveLabelItem;

/**
 * Manu item collection for a GeoElement
 * 
 * @author Zbynek
 */
public class GeoElementMenuItemCollection
		extends MenuItemCollection<GeoElement> {

	/**
	 * Add "Add label" / "Remove label" to a menu
	 * 
	 */
	public void addLabelingActions(Runnable actionCallback) {
		addItems(new RemoveLabelItem(actionCallback), new AddLabelItem(actionCallback));
	}
}
