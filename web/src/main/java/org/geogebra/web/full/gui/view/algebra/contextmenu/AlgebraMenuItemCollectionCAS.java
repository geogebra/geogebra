package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.CreateSliderItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.RemoveSliderItem;
import org.geogebra.web.full.gui.view.algebra.contextmenu.item.SolveItem;

/**
 * AV menu items for CAS
 */
public class AlgebraMenuItemCollectionCAS extends AlgebraMenuItemCollection {

	/**
	 * @param algebraView algebra view
	 */
	public AlgebraMenuItemCollectionCAS(AlgebraViewW algebraView) {
		super(algebraView);
		AlgebraProcessor processor = algebraView.getApp().getKernel().getAlgebraProcessor();
		addAction(0, new SolveItem());
		addAction(6, new CreateSliderItem(processor));
		addAction(6, new RemoveSliderItem(processor));

	}
}
