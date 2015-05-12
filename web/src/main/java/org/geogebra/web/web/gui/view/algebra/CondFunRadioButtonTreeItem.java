package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
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
 * CondFunRadioButtonTreeItem for creating piecewise functions (conditional
 * functions, .isGeoFunctionConditional()) in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class CondFunRadioButtonTreeItem extends RadioButtonTreeItem {

	FlowPanel auxPanel;

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
				AppResources.INSTANCE.point_cross()));
		btnRow.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent mde) {
				mde.preventDefault();
				mde.stopPropagation();
				addNewRow();
			}
		});
		btnRow.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent moe) {
				moe.preventDefault();
				moe.stopPropagation();
				((DrawEquationWeb) app.getDrawEquation()).setMouseOut(false);
			}
		});
		// btnRow.addMouseMoveHandler(new MouseMoveHandler() {
		// public void onMouseMove(MouseMoveEvent moe) {
		// moe.preventDefault();
		// moe.stopPropagation();
		// ((DrawEquationWeb) app.getDrawEquation()).setMouseOut(false);
		// }
		// });

		// basically, everything except onClick,
		// static to prevent more instances
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

		btnRow.addStyleName("RadioButtonTreeItemSpecButton");
		auxPanel = new FlowPanel();
		auxPanel.add(btnRow);
		auxPanel.addStyleName("RadioButtonTreeItemSpecButtonPanel");
		auxPanel.setVisible(false);
		add(auxPanel);
		ihtml.getElement().appendChild(auxPanel.getElement());
	}

	public void addNewRow() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {

				// could probably implement this for non-editing case
				// better (like in MatrixRadioButtonTreeItem),
				// but now it's only used in editing mode anyway
				if (!commonEditingCheck())
					ensureEditing();

				DrawEquationWeb.addNewRowToMatrix(seMayLatex);
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
