package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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

	static class CancelEvents extends Object implements MouseDownHandler,
			MouseUpHandler, MouseOverHandler, MouseOutHandler,
			MouseMoveHandler, ClickHandler, DoubleClickHandler,
			TouchStartHandler,
			TouchEndHandler, TouchMoveHandler {
		public void onClick(ClickEvent ce) {
			ce.preventDefault();
			ce.stopPropagation();
		}

		public void onDoubleClick(DoubleClickEvent me) {
			me.preventDefault();
			me.stopPropagation();
		}

		public void onMouseDown(MouseDownEvent me) {
			me.preventDefault();
			me.stopPropagation();
		}

		public void onMouseUp(MouseUpEvent mue) {
			mue.preventDefault();
			mue.stopPropagation();
		}

		public void onMouseOver(MouseOverEvent me) {
			me.preventDefault();
			me.stopPropagation();
		}

		public void onMouseOut(MouseOutEvent mue) {
			mue.preventDefault();
			mue.stopPropagation();
		}

		public void onMouseMove(MouseMoveEvent mue) {
			mue.preventDefault();
			mue.stopPropagation();
		}

		public void onTouchStart(TouchStartEvent tse) {
			tse.preventDefault();
			tse.stopPropagation();
		}

		public void onTouchEnd(TouchEndEvent tee) {
			tee.preventDefault();
			tee.stopPropagation();
		}

		public void onTouchMove(TouchMoveEvent tee) {
			tee.preventDefault();
			tee.stopPropagation();
		}
	}

	static CancelEvents cancelEvents = new CancelEvents();

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
		btnRow.addClickHandler(cancelEvents);
		btnRow.addDoubleClickHandler(cancelEvents);
		// btnRow.addMouseDownHandler(cancelEvents);
		btnRow.addMouseUpHandler(cancelEvents);
		btnRow.addMouseMoveHandler(cancelEvents);
		// btnRow.addMouseOverHandler(cancelEvents);
		btnRow.addMouseOutHandler(cancelEvents);
		btnRow.addTouchStartHandler(cancelEvents);
		btnRow.addTouchEndHandler(cancelEvents);
		btnRow.addTouchMoveHandler(cancelEvents);

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

				if (!commonEditingCheck())
					ensureEditing();

				DrawEquationWeb.addNewRowToMatrix(seMayLatex);
			}
		});
	}
}
