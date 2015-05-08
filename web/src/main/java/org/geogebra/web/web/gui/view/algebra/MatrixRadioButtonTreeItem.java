package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.main.DrawEquationWeb;

import com.google.gwt.event.dom.client.DoubleClickEvent;
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
	/*
	 * public MatrixRadioButtonTreeItem(Kernel kern) { // note that this should
	 * create a 2x2 Zero matrix by default! // so what about creating the same
	 * matrix first, and reuse // existing code for constructor / editing?
	 * 
	 * // super(kern, create2x2ZeroMatrix(kern), AppResources.INSTANCE.shown()
	 * // .getSafeUri(), // AppResources.INSTANCE.hidden().getSafeUri());
	 * 
	 * // or... call even more general code! this(create2x2ZeroMatrix(kern),
	 * AppResources.INSTANCE.shown() .getSafeUri(),
	 * AppResources.INSTANCE.hidden().getSafeUri()); }
	 */

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

	public static GeoList create2x2ZeroMatrix(Kernel kern) {
		// this works in a similar was as AlgoIdentity
		GeoList ret = new GeoList(kern.getConstruction());
		GeoList row = new GeoList(kern.getConstruction());
		row.add(new GeoNumeric(kern.getConstruction(), 0));
		row.add(new GeoNumeric(kern.getConstruction(), 0));
		ret.add(row);
		row = new GeoList(kern.getConstruction());
		row.add(new GeoNumeric(kern.getConstruction(), 0));
		row.add(new GeoNumeric(kern.getConstruction(), 0));
		ret.add(row);
		ret.setLabel(ret.getDefaultLabel());
		return ret;
	}

	public void increaseRows() {
		DrawEquationWeb.addNewRowToMatrix(seMayLatex);
	}

	public void increaseCols() {

	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		super.onDoubleClick(evt);
		// only for testing!
		// increaseRows();
	}
}
