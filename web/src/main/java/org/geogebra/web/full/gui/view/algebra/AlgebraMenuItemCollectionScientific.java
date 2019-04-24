package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DeleteAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DuplicateAction;

/**
 * AV context menu actions for scientific
 *
 * @author Zbynek
 *
 */
public class AlgebraMenuItemCollectionScientific extends MenuActionCollection<GeoElement> {

	/**
	 * @param av
	 *            algebra view
	 */
	public AlgebraMenuItemCollectionScientific(AlgebraViewW av) {
		AlgebraMenuItemCollection.addLabelingActions(this);
		addActions(new DuplicateAction(av),
				new DeleteAction());
	}
}
