package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
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
	/*
	 * public CondFunRadioButtonTreeItem(Kernel kern) { super(kern, null,
	 * AppResources.INSTANCE.shown().getSafeUri(),
	 * AppResources.INSTANCE.hidden().getSafeUri()); }
	 */

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public CondFunRadioButtonTreeItem(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);

		PushButton btnRow = new PushButton(new Image(
				AppResources.INSTANCE.point_cross()), new ClickHandler() {
			public void onClick(ClickEvent ce) {
				addNewRow();
				ce.preventDefault();
				ce.stopPropagation();
			}
		});
		btnRow.addStyleName("RadioButtonTreeItemSpecButton");
		FlowPanel auxPanel = new FlowPanel();
		auxPanel.add(btnRow);
		auxPanel.addStyleName("RadioButtonTreeItemSpecButtonPanel");
		add(auxPanel);
		ihtml.getElement().appendChild(auxPanel.getElement());
	}

	public void addNewRow() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				DrawEquationWeb.addNewRowToMatrix(seMayLatex);

				// it seems clicking elsewhere closes editing mode,
				// but we need to be there in case of piecewise functions!
				ensureEditing();
			}
		});
	}
}
