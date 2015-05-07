package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.safehtml.shared.SafeUri;

/**
 * CondFunRadioButtonTreeItem for creating piecewise functions (conditional
 * functions, .isGeoFunctionConditional()) in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class CondFunRadioButtonTreeItem extends RadioButtonTreeItem {

	/**
	 * Creating a SpecialRadioButtonTreeItem from scratch as this should be
	 * possible when the user clicks on Algebra View GUI buttons designed for
	 * this purpose - this should be empty and editable
	 */
	public CondFunRadioButtonTreeItem(Kernel kern) {
		super(kern, null, AppResources.INSTANCE.shown().getSafeUri(),
				AppResources.INSTANCE.hidden().getSafeUri());
	}

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public CondFunRadioButtonTreeItem(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);
	}
}
