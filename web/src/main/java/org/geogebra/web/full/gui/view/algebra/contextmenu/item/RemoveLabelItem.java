package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveLabelAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;

/**
 * Removes label from a a geo in AV
 */
public class RemoveLabelItem extends MenuItem<GeoElement> {

	/**
	 * New remove label action
	 */
	public RemoveLabelItem(Runnable actionCallback) {
		super("RemoveLabel", new RemoveLabelAction(), actionCallback);
	}
}
