package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DuplicateOutputAction;

/**
 * Duplicates geo in AV and puts focus to the new input
 */
public class DuplicateOutputItem extends MenuItem<GeoElement> {

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public DuplicateOutputItem(AlgebraViewW algebraView) {
		super("ContextMenu.DuplicateOutput",
				MaterialDesignResources.INSTANCE.duplicate_black(),
				new DuplicateOutputAction(algebraView));
	}
}
