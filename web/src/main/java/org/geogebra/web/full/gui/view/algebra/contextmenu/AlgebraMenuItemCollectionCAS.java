package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;

public class AlgebraMenuItemCollectionCAS extends AlgebraMenuItemCollection {

	public AlgebraMenuItemCollectionCAS(AlgebraViewW algebraView) {
		super(algebraView);
		addAction(0, new SolveAction());
	}

}
