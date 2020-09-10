package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DeleteItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DuplicateInputItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.DuplicateOutputItem;

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
		addItems(new DuplicateInputItem(av), new DuplicateOutputItem(av), new DeleteItem());
	}
}
