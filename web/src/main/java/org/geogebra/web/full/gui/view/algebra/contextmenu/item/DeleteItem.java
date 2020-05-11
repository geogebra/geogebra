package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DeleteAction;

/**
 * Deletes a geo
 */
public class DeleteItem extends MenuItem<GeoElement> {

	/**
	 * New delete action
	 */
	public DeleteItem() {
		super("Delete",
				MaterialDesignResources.INSTANCE.delete_black(),
				new DeleteAction());
	}
}
