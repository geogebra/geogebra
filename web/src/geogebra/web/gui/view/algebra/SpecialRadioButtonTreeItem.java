package geogebra.web.gui.view.algebra;

import geogebra.common.kernel.Kernel;
import geogebra.web.gui.images.AppResources;

/**
 * SpecialRadioButtonTreeItem for creating special formulas in the algebra view
 * e.g. matrix, piecewise function, parametric curve
 * 
 * File created by Arpad Fekete
 */
public class SpecialRadioButtonTreeItem extends RadioButtonTreeItem {

	public SpecialRadioButtonTreeItem(Kernel kern) {
		super(kern, null, AppResources.INSTANCE.shown().getSafeUri(),
		        AppResources.INSTANCE.hidden().getSafeUri());
	}
}
