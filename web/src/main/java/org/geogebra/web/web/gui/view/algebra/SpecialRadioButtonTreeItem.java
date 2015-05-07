package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.safehtml.shared.SafeUri;

/**
 * SpecialRadioButtonTreeItem for creating special formulas in the algebra view
 * e.g. matrix, piecewise function, parametric curve
 * 
 * File created by Arpad Fekete
 */
public class SpecialRadioButtonTreeItem extends RadioButtonTreeItem {

	/**
	 * Creating a SpecialRadioButtonTreeItem from scratch as this should be
	 * possible when the user clicks on Algebra View GUI buttons designed for
	 * this purpose - this should be empty and editable
	 */
	public SpecialRadioButtonTreeItem(Kernel kern) {
		super(kern, null, AppResources.INSTANCE.shown().getSafeUri(),
		        AppResources.INSTANCE.hidden().getSafeUri());
	}

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public SpecialRadioButtonTreeItem(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);
	}
}
