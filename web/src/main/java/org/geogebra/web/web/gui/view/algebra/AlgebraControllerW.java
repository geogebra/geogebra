package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.algebra.AlgebraController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.web.gui.GuiManagerW;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;


/**
 * Algebra controller for web;
 *
 */
public class AlgebraControllerW extends AlgebraController
		implements MouseMoveHandler, MouseDownHandler, TouchStartHandler,
		TouchEndHandler,
		TouchMoveHandler, LongTouchHandler {

	private LongTouchManager longTouchManager;

	/**
	 * @param kernel
	 *            kernel
	 */
	public AlgebraControllerW(Kernel kernel) {
		super(kernel);
		longTouchManager = LongTouchManager.getInstance();
	}

	@Override
	public void handleLongTouch(int x, int y) {
		PointerEvent event = new PointerEvent(x, y, PointerEventType.TOUCH, ZeroOffset.instance);
		event.setIsRightClick(true);
		mousePressed(event);
	}


	private void mousePressed(AbstractEvent e) {
		getView().cancelEditItem();
		boolean rightClick = app.isRightClickEnabled() && e.isRightClick();

		// RIGHT CLICK
		if (rightClick) {
			// The default algebra menu will be created here (not for GeoElements).
			// LEFT CLICK	
		} else {
			
			//hide dialogs if they are open
			((GuiManagerW)app.getGuiManager()).removePopup();

			// When a single, new selection is made with no key modifiers
			// we need to handle selection in mousePressed, not mouseClicked.
			// By doing this selection early, a DnD drag will come afterwards
			// and grab the new selection. 
			// All other selection types must be handled later in mouseClicked. 
			// In this case a DnD drag starts first and grabs the previously selected 
			// geos (e.g. cntrl-selected or EV selected) as the user expects.

			// skipSelection = false; // flag to prevent duplicate selection in
			// MouseClicked

			// view.getPathForLocation is not yet implemented, but if it will be, note Window.getScrollLeft() (ticket #4049)

		}

	}



	//=====================================================
	// Drag and Drop 
	//=====================================================

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		event.preventDefault();
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mousePressed(PointerEvent.wrapEventAbsolute(event, ZeroOffset.instance));
	}



	@Override
	public void onTouchMove(TouchMoveEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent e = PointerEvent.wrapEvent(targets.get(targets.length()-1), ZeroOffset.instance);
		Element el = Element.as(event.getNativeEvent().getEventTarget());
		
		if (el == ((AlgebraViewW) getView()).getElement()) {
			longTouchManager.rescheduleTimerIfRunning(this, e.getX(), e.getY());
		}
		CancelEventTimer.touchEventOccured();
    }

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		CancelEventTimer.touchEventOccured();
    }

	@Override
	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent e = PointerEvent.wrapEvent(targets.get(0), ZeroOffset.instance);
		Element el = Element.as(event.getNativeEvent().getEventTarget());
		
		if (el == ((AlgebraViewW) getView()).getElement()) {
			longTouchManager.scheduleTimer(this, e.getX(), e.getY());
		}
		mousePressed(e);
		CancelEventTimer.touchEventOccured();
    }

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
	}

}
