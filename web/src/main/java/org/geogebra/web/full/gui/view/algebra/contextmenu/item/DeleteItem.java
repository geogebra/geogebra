package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DeleteAction;

/**
 * Deletes a geo
 */
public class DeleteItem extends MenuItem<GeoElement> {

	/**
	 * New delete action
	 */
	public DeleteItem(Runnable actionCallback) {
		super("Delete",
				new DeleteAction(), actionCallback);
	}
}
