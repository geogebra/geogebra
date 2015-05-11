package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

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
		PushButton btnRow = new PushButton(new Image(
				AppResources.INSTANCE.point_down()), new ClickHandler() {
			public void onClick(ClickEvent ce) {
				increaseRows();
			}
		});
		btnRow.addStyleName("RadioButtonTreeItemSpecButton");
		PushButton btnCol = new PushButton(new Image(
				AppResources.INSTANCE.point_right()), new ClickHandler() {
			public void onClick(ClickEvent ce) {
				increaseCols();
			}
		});
		btnCol.addStyleName("RadioButtonTreeItemSpecButton");
		FlowPanel auxPanel = new FlowPanel();
		auxPanel.add(btnRow);
		auxPanel.add(btnCol);
		auxPanel.addStyleName("RadioButtonTreeItemSpecButtonPanel");
		add(auxPanel);
		// not working
		ihtml.getElement().appendChild(auxPanel.getElement());
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
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				DrawEquationWeb.addNewRowToMatrix(seMayLatex);
			}
		});
	}

	public void increaseCols() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				DrawEquationWeb.addNewColToMatrix(seMayLatex);
			}
		});
	}
}
