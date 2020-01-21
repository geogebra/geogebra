package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DeleteAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DuplicateAction;

/**
 * AV context menu actions for scientific
 *
 * @author Zbynek
 *
 */
public class AlgebraMenuItemCollectionScientific
		extends GeoElementMenuItemCollection {

	/**
	 * @param av
	 *            algebra view
	 */
	public AlgebraMenuItemCollectionScientific(AlgebraViewW av) {
		addLabelingActions();
		addActions(new DuplicateAction(av), new DeleteAction());
	}
}
