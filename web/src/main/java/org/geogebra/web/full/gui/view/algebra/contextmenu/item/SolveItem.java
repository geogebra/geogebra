package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.SolveAction;

/**
 * Solves one or more equations.
 */
public class SolveItem extends MenuItem<GeoElement> {

	/**
	 * New solve action
	 */
	public SolveItem() {
		super("Solve", new SolveAction());
	}
}
