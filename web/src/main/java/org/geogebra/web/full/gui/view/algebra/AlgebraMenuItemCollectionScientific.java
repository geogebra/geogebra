package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.full.gui.view.algebra.contextmenu.DeleteAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.DuplicateAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.RemoveLabelAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.AddLabelAction;

/**
 * AV context menu actions for scientific
 *
 * @author Zbynek
 *
 */
public class AlgebraMenuItemCollectionScientific extends MenuActionCollection {

	/**
	 * @param av
	 *            algebra view
	 */
	public AlgebraMenuItemCollectionScientific(AlgebraViewW av) {
		addActions(new RemoveLabelAction(), new AddLabelAction(), new DuplicateAction(av),
				new DeleteAction());
	}
}
