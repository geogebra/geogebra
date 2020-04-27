package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.CreateSliderAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.RemoveSliderAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.SolveAction;

/**
 * AV menu items for CAS
 */
public class AlgebraMenuItemCollectionCAS extends AlgebraMenuItemCollection {

	/**
	 * @param algebraView
	 *            algebra view
	 */
	public AlgebraMenuItemCollectionCAS(AlgebraViewW algebraView) {
		super(algebraView);
		addAction(0, new SolveAction());
		addAction(5, new CreateSliderAction());
		addAction(5, new RemoveSliderAction());
	}

}
