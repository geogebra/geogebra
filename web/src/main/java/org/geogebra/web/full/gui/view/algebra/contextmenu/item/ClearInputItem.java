package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.ClearInputAction;

/**
 * Clears input row
 * @author laszlo
 *
 */
public class ClearInputItem extends MenuItem<GeoElement> {

	/**
	 * @param inputItem
	 *            input item
	 */
	public ClearInputItem(RadioTreeItem inputItem) {
		super("Delete",
				MaterialDesignResources.INSTANCE.delete_black(),
				new ClearInputAction(inputItem));
	}
}
