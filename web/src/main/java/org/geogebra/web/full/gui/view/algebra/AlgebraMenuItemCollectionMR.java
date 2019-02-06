package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DeleteAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DuplicateAction;

/**
 * Algebra context menu actions for Windows MR
 *
 * @author Zbynek
 */
public class AlgebraMenuItemCollectionMR extends MenuActionCollection<GeoElement> {

	/**
	 * @param av
	 *            algebra view
	 */
	public AlgebraMenuItemCollectionMR(AlgebraViewW av) {
		addActions(new DuplicateAction(av), new DeleteAction());
	}
}
