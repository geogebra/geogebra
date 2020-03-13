package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.SolveItem;

/**
 * AV menu items for 3D
 */
public class AlgebraMenuItemCollection3D extends AlgebraMenuItemCollection {

	/**
	 * @param algebraView algebra view
	 */
	public AlgebraMenuItemCollection3D(AlgebraViewW algebraView) {
		super(algebraView);
		addAction(0, new SolveItem());
	}
}
