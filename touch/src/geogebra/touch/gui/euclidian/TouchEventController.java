package geogebra.touch.gui.euclidian;

import geogebra.common.euclidian.event.PointerEventType;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;

class TouchEventController implements TouchStartHandler, TouchMoveHandler,
		TouchEndHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler,
		MouseWheelHandler, MouseOutHandler {

	private static int getX(final Touch touch) {
		return touch.getClientX();
	}

	private final TouchController mc;

	private final Widget offsetWidget;
	private boolean ignoreMouseEvents = false;

	TouchEventController(final TouchController mc, final Widget w) {
		this.mc = mc;
		this.offsetWidget = w;
	}

	private int getY(final Touch touch) {
		final int y = touch.getClientY();
		return this.offsetWidget == null ? y : y
				- TouchEntryPoint.getLookAndFeel().getTabletHeaderHeight();
	}

	// Listeners for Desktop
	@Override
	public void onMouseDown(final MouseDownEvent event) {
		// ensure textfields lose focus
		if (!TouchEventController.this.mc.isTextfieldHasFocus()) {
			event.preventDefault();
		}
		//IE gives double coordinates that cannot be handled by pen => round needed
		if (!TouchEventController.this.ignoreMouseEvents) {
			TouchEventController.this.mc.onTouchStart(Math.round(event.getX()),
					Math.round(event.getY()),PointerEventType.MOUSE);
		}
	}

	@Override
	public void onMouseMove(final MouseMoveEvent event) {
		//IE gives double coordinates that cannot be handled by pen => round needed
		if (!this.ignoreMouseEvents) {
			this.mc.onTouchMove(Math.round(event.getX()), Math.round(event.getY()), PointerEventType.MOUSE);
		}
	}

	@Override
	public void onMouseUp(final MouseUpEvent event) {
		//IE gives double coordinates that cannot be handled by pen => round needed
		if (!TouchEventController.this.ignoreMouseEvents) {
			TouchEventController.this.mc.onTouchEnd(Math.round(event.getX()), Math.round(event.getY()),
					PointerEventType.MOUSE);
		}
	}

	@Override
	public void onMouseWheel(final MouseWheelEvent event) {
		if (event.getDeltaY() > 0) {
			this.mc.onPinch(event.getX(), event.getY(), 0.9);
		} else {
			this.mc.onPinch(event.getX(), event.getY(), 1.1);
		}
	}

	@Override
	public void onTouchEnd(final TouchEndEvent event) {
		event.preventDefault();
		this.mc.onTouchEnd(getX(event.getChangedTouches().get(0)),
				this.getY(event.getChangedTouches().get(0)), PointerEventType.TOUCH);
	}

	@Override
	public void onTouchMove(final TouchMoveEvent event) {
		event.preventDefault();
		
		if (event.getTouches().length() == 1) {
			// proceed normally
			this.mc.onTouchMove(getX(event.getTouches().get(0)),
					this.getY(event.getTouches().get(0)),PointerEventType.TOUCH);
		} else if (event.getTouches().length() == 2) {
			Touch t1 = event.getTouches().get(0);
			Touch t2 = event.getTouches().get(1);
			this.mc.twoTouchMove(getX(t1), getY(t1), getX(t2), getY(t2));
		}
	}

	@Override
	public void onTouchStart(final TouchStartEvent event) {
		if (event.getTouches().length() == 1) {
			// ensure textfiled loses focus
			if (!this.mc.isTextfieldHasFocus()) {
				event.preventDefault();
			}
			this.ignoreMouseEvents = true;
			this.mc.onTouchStart(getX(event.getTouches().get(0)),
					this.getY(event.getTouches().get(0)),PointerEventType.TOUCH);
		} else if (event.getTouches().length() == 2) {
			Touch t1 = event.getTouches().get(0);
			Touch t2 = event.getTouches().get(1);
			this.mc.twoTouchStart(getX(t1), getY(t1), getX(t2), getY(t2));
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		this.mc.onMouseExited();
		
	}

}
