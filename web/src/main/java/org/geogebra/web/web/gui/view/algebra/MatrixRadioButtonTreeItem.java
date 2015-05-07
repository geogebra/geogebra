package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.safehtml.shared.SafeUri;

/**
 * MatrixRadioButtonTreeItem for creating matrices (2-dimensional lists in the
 * algebra view
 * 
 * File created by Arpad Fekete
 */
public class MatrixRadioButtonTreeItem extends RadioButtonTreeItem {

	/**
	 * Creating a SpecialRadioButtonTreeItem from scratch as this should be
	 * possible when the user clicks on Algebra View GUI buttons designed for
	 * this purpose - this should be empty and editable
	 */
	public MatrixRadioButtonTreeItem(Kernel kern) {
		super(kern, null, AppResources.INSTANCE.shown().getSafeUri(),
		        AppResources.INSTANCE.hidden().getSafeUri());
	}

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public MatrixRadioButtonTreeItem(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);
	}

	public void increaseRows() {

	}

	public void increaseCols() {

	}
}
