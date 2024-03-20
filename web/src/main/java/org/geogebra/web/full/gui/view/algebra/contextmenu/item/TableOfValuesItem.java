package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.TableOfValuesAction;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;

/**
 * Adds a functionable geo to the table view
 */
public class TableOfValuesItem extends MenuItem<GeoElement> {

	/**
	 * New table view action
	 */
	public TableOfValuesItem(GuiManagerInterfaceW guiManager) {
		super("TableOfValues", new TableOfValuesAction(guiManager));
	}
}