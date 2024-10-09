package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.gui.view.algebra.contextmenu.impl.AddLabelAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;

/**
 * Add label to a geo in AV
 */
public class AddLabelItem extends MenuItem<GeoElement> {

	/**
	 * New add label action
	 */
	public AddLabelItem(Runnable actionCallback) {
		super("AddLabel", new AddLabelAction(), actionCallback);
	}
}
