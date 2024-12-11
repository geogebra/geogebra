package org.geogebra.web.html5.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.gwtproject.dom.client.Touch;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.event.shared.HandlerRegistrations;
import org.gwtproject.user.client.ui.Widget;

/**
 * Handler for mouse up / touch end events
 */
public abstract class ClickEndHandler {

	/**
	 * Attaches a handler for MouseUpEvent and a TouchEndEvent to the widget.
	 * CancelEventTimer is used to prevent duplication of events.
	 * 
	 * @param w
	 *            Widget that the handlers are attached to
	 * @param handler
	 *            EventHandler (instance of this class)
	 * @return registration that makes it possible to remove both mouse and
	 *         touch handler
	 */
	public static HandlerRegistration init(Widget w,
			final ClickEndHandler handler) {
		final HandlerRegistration mouseReg = w.addDomHandler(
				event -> {
					if (handler.preventDefault) {
						event.preventDefault();
					}
					if (handler.stopPropagation) {
						event.stopPropagation();
					}
					if (!CancelEventTimer.cancelMouseEvent()) {
						handler.onClickEnd(event.getX(), event.getY(),
								PointerEventType.MOUSE);
					}
				}, MouseUpEvent.getType());

		final HandlerRegistration touchReg = w.addBitlessDomHandler(
				event -> {
					if (handler.preventDefault) {
						event.preventDefault();
					}
					if (handler.stopPropagation) {
						event.stopPropagation();
					}
					Touch removedTouch = event.getChangedTouches().get(0);
					handler.onClickEnd(removedTouch.getClientX(),
							removedTouch.getClientY(),
							PointerEventType.TOUCH);
					CancelEventTimer.touchEventOccurred();
				}, TouchEndEvent.getType());
		return HandlerRegistrations.compose(mouseReg, touchReg);
	}

	/** whether default browser behavior needs preventing */
	boolean preventDefault = false;
	/** whether to stop propagation */
	boolean stopPropagation = false;

	/**
	 * creates the base version of a ClickEventHandler.
	 */
	public ClickEndHandler() {
	}

	/**
	 * {@link ClickEndHandler} with preventDefault and stopPropagation set
	 * explicitly. event.preventDefault() and event.stopPropagation() will also
	 * be called, if the handling-method is canceled for the event.
	 * 
	 * @param preventDefault
	 *            whether event.preventDefault() should be called for
	 *            MouseUpEvents and TouchEndEvents
	 * @param stopPropagation
	 *            whether event.stopPropagation() should be called for
	 *            MouseUpEvents and TouchEndEvents
	 */
	public ClickEndHandler(boolean preventDefault, boolean stopPropagation) {
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
	public abstract void onClickEnd(int x, int y, PointerEventType type);

	/**
	 * Set preventDefault explicitly. event.preventDefault() will also be
	 * called, if the handling-method is canceled for the event.
	 * 
	 * @param preventDefault
	 *            whether event.preventDefault() should be called for
	 *            MouseUpEvents and TouchEndEvents
	 */
	public void setPreventDefault(boolean preventDefault) {
		this.preventDefault = preventDefault;
	}

	/**
	 * Set stopPropagation explicitly. event.stopPropagation() will also be
	 * called, if the handling-method is canceled for the event.
	 * 
	 * @param stopPropagation
	 *            whether event.stopPropagation() should be called for
	 *            MouseUpEvents and TouchEndEvents
	 */
	public void setStopPropagation(boolean stopPropagation) {
		this.stopPropagation = stopPropagation;
	}

	/**
	 * Attaches a handler only for preventing default and/or stopping
	 * propagation.
	 *
	 * @param w
	 *            Widget that the handlers are attached to
	 * @param preventDefault
	 *            whether event.preventDefault() should be called for
	 *            MouseUpEvents and TouchEndEvents
	 * @param stopPropagation
	 *            whether event.stopPropagation() should be called for
	 *            MouseUpEvents and TouchEndEvents
	 */
	public static void initDefaults(Widget w, boolean preventDefault,
									boolean stopPropagation) {
		init(w, new ClickEndHandler(preventDefault, stopPropagation) {

			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				// just for preventDefault and stopPropagation.
			}
		});
	}
}
