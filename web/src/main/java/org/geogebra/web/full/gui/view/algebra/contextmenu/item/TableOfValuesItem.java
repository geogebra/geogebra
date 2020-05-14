package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.TableOfValuesAction;

/**
 * Adds a functionable geo to the table view
 */
public class TableOfValuesItem extends MenuItem<GeoElement> {

	/**
	 * New table view action
	 */
	public TableOfValuesItem() {
		super("TableOfValues", new TableOfValuesAction());
	}
}