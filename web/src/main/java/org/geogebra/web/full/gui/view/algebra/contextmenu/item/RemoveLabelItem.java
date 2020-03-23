package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.RemoveLabelAction;

/**
 * Removes label from a a geo in AV
 */
public class RemoveLabelItem extends MenuItem<GeoElement> {

	/**
	 * New remove label action
	 */
	public RemoveLabelItem() {
		super("RemoveLabel", new RemoveLabelAction());
	}
}
