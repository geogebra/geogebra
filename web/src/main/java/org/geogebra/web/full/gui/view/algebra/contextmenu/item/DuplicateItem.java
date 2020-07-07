package org.geogebra.web.full.gui.view.algebra.contextmenu.item;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DuplicateAction;

/**
 * Duplicates geo in AV and puts focus to the new input
 */
public class DuplicateItem extends MenuItem<GeoElement> {

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public DuplicateItem(AlgebraViewW algebraView) {
		super("Duplicate",
				MaterialDesignResources.INSTANCE.duplicate_black(),
				new DuplicateAction(algebraView));
	}
}
