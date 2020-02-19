package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuItemCollection;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DeleteItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DuplicateItem;

/**
 * Algebra context menu actions for Windows MR
 *
 * @author Zbynek
 */
public class AlgebraMenuItemCollectionMR extends MenuItemCollection<GeoElement> {

	/**
	 * @param av
	 *            algebra view
	 */
	public AlgebraMenuItemCollectionMR(AlgebraViewW av) {
		addItems(new DuplicateItem(av), new DeleteItem());
	}
}
