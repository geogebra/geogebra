package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.AddLabelAction;

/**
 * Add label to a geo in AV
 */
public class AddLabelItem extends MenuItem<GeoElement> {

	/**
	 * New add label action
	 */
	public AddLabelItem() {
		super("AddLabel", new AddLabelAction());
	}
}
