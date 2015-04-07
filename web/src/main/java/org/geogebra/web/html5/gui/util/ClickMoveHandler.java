package org.geogebra.web.html5.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class ClickMoveHandler {

	/**
	 * Attaches a handler for MouseMoveEvent and a TouchMoveEvent to the widget.
	 * CancelEventTimer is used to prevent duplication of events.
	 * 
	 * @param w
	 *            : Widget that the handlers are attached to
	 * @param handler
	 *            : EventHandler (instance of this class)
	 */
	public static void init(Widget w, final ClickMoveHandler handler) {
		w.addDomHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (handler.preventDefault) {
					event.preventDefault();
				}
				if (handler.stopPropagation) {
					event.stopPropagation();
				}
				if (!CancelEventTimer.cancelMouseEvent()) {
					handler.onClickMove(event.getX(), event.getY(),
					        PointerEventType.MOUSE);
				}
			}
		}, MouseMoveEvent.getType());

		w.addDomHandler(new TouchMoveHandler() {
			public void onTouchMove(TouchMoveEvent event) {
				if (handler.preventDefault) {
					event.preventDefault();
				}
				if (handler.stopPropagation) {
					event.stopPropagation();
				}
				handler.onClickMove(event.getTouches().get(0).getClientX(),
				        event.getTouches().get(0).getClientY(),
				        PointerEventType.TOUCH);
				CancelEventTimer.touchEventOccured();
			}
		}, TouchMoveEvent.getType());
	}

	boolean preventDefault = false;
	boolean stopPropagation = false;

	/**
	 * creates the base version of a ClickEventHandler.
	 */
	public ClickMoveHandler() {
	}

	/**
	 * {@link ClickMoveHandler} with preventDefault and stopPropagation set
	 * explicitly. event.preventDefault() and event.stopPropagation() will also
	 * be called, if the handling-method is canceled for the event.
	 * 
	 * @param preventDefault
	 *            whether or not event.preventDefault() should be called for
	 *            MouseMoveEvents and TouchMoveEvents
	 * @param stopPropagation
	 *            whether or not event.stopPropagation() should be called for
	 *            MouseMoveEvents and TouchMoveEvents
	 */
	public ClickMoveHandler(boolean preventDefault, boolean stopPropagation) {
		this.preventDefault = preventDefault;
		this.stopPropagation = stopPropagation;
	}

	/**
	 * Actual handler-method, needs to be overwritten in the instances.
	 * 
	 * @param x
	 *            x-coordinate of the event
	 * @param y
	 *            y-coordinate of the event
	 * @param type
	 *            type of the event
	 */
	public abstract void onClickMove(int x, int y, PointerEventType type);

	/**
	 * Set preventDefault explicitly. event.preventDefault() will also be
	 * called, if the handling-method is canceled for the event.
	 * 
	 * @param preventDefault
	 *            whether or not event.preventDefault() should be called for
	 *            MouseMoveEvents and TouchMoveEvents
	 */
	public void setPreventDefault(boolean preventDefault) {
		this.preventDefault = preventDefault;
	}

	/**
	 * Set stopPropagation explicitly. event.stopPropagation() will also be
	 * called, if the handling-method is canceled for the event.
	 * 
	 * @param stopPropagation
	 *            whether or not event.stopPropagation() should be called for
	 *            MouseMoveEvents and TouchMoveEvents
	 */
	public void setStopPropagation(boolean stopPropagation) {
		this.stopPropagation = stopPropagation;
	}
}
