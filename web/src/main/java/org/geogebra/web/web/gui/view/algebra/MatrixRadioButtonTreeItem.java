package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.gui.util.CancelEvents;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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

	FlowPanel auxPanel;

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public MatrixRadioButtonTreeItem(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);

		MouseOverHandler noout = new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent moe) {
				moe.preventDefault();
				moe.stopPropagation();
				((DrawEquationWeb) app.getDrawEquation()).setMouseOut(false);
			}
		};

		PushButton btnRow = new PushButton(new Image(
				AppResources.INSTANCE.point_down()));
		btnRow.addStyleName("RadioButtonTreeItemSpecButton");
		btnRow.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent e) {
				e.preventDefault();
				e.stopPropagation();
				increaseRows();
			}
		});
		btnRow.addMouseOverHandler(noout);
		btnRow.addClickHandler(CancelEvents.instance);
		btnRow.addDoubleClickHandler(CancelEvents.instance);
		// btnRow.addMouseDownHandler(cancelEvents);
		btnRow.addMouseUpHandler(CancelEvents.instance);
		btnRow.addMouseMoveHandler(CancelEvents.instance);
		// btnRow.addMouseOverHandler(cancelEvents);
		btnRow.addMouseOutHandler(CancelEvents.instance);
		btnRow.addTouchStartHandler(CancelEvents.instance);
		btnRow.addTouchEndHandler(CancelEvents.instance);
		btnRow.addTouchMoveHandler(CancelEvents.instance);

		PushButton btnCol = new PushButton(new Image(
				AppResources.INSTANCE.point_right()));
		btnCol.addStyleName("RadioButtonTreeItemSpecButton");
		btnCol.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent e) {
				e.preventDefault();
				e.stopPropagation();
				increaseCols();
			}
		});
		btnCol.addMouseOverHandler(noout);
		btnCol.addClickHandler(CancelEvents.instance);
		btnCol.addDoubleClickHandler(CancelEvents.instance);
		// btnCol.addMouseDownHandler(cancelEvents);
		btnCol.addMouseUpHandler(CancelEvents.instance);
		btnCol.addMouseMoveHandler(CancelEvents.instance);
		// btnCol.addMouseOverHandler(cancelEvents);
		btnCol.addMouseOutHandler(CancelEvents.instance);
		btnCol.addTouchStartHandler(CancelEvents.instance);
		btnCol.addTouchEndHandler(CancelEvents.instance);
		btnCol.addTouchMoveHandler(CancelEvents.instance);

		auxPanel = new FlowPanel();
		auxPanel.add(btnRow);
		auxPanel.add(btnCol);
		auxPanel.addStyleName("RadioButtonTreeItemSpecButtonPanel");
		auxPanel.setVisible(false);
		add(auxPanel);
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
				boolean wasEditing = commonEditingCheck();

				if (!wasEditing)
					ensureEditing();

				DrawEquationWeb.addNewRowToMatrix(seMayLatex);

				// it is a good question whether shall we save the result
				// in a permanent way, and in which case (wasEditing?)
				// why not?
				DrawEquationWeb.endEditingEquationMathQuillGGB(
						MatrixRadioButtonTreeItem.this, seMayLatex);

				if (wasEditing) {
					av.startEditing(geo);
				}
			}
		});
	}

	public void increaseCols() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				boolean wasEditing = commonEditingCheck();

				if (!wasEditing)
					ensureEditing();

				DrawEquationWeb.addNewColToMatrix(seMayLatex);

				// it is a good question whether shall we save the result
				// in a permanent way, and in which case (wasEditing?)
				// why not?
				DrawEquationWeb.endEditingEquationMathQuillGGB(
						MatrixRadioButtonTreeItem.this, seMayLatex);

				if (wasEditing) {
					av.startEditing(geo);
				}
			}
		});
	}

	@Override
	public void startEditing() {
		super.startEditing();
		auxPanel.setVisible(true);
	}

	@Override
	public boolean stopEditing(String s) {
		auxPanel.setVisible(false);
		return super.stopEditing(s);
	}

	@Override
	public void stopEditingSimple(String s) {
		auxPanel.setVisible(false);
		super.stopEditingSimple(s);
	}
}
