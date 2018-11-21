package org.geogebra.web.html5.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * Listener for MouseDown and TouchStart
 *
 */
public abstract class ClickStartHandler {
	/** whether to prevent default behavior */
	boolean preventDefault = false;
	/** whether to stop propagation */
	boolean stopPropagation = false;
	/**
	 * Attaches a handler for MouseDownEvents and a TouchStartEvents to the
	 * widget. CancelEventTimer is used to prevent duplication of events.
	 * 
	 * @param w
	 *            Widget that the handlers are attached to
	 * @param handler
	 *            EventHandler (instance of this class)
	 */
	public static void init(Widget w, final ClickStartHandler handler) {
		w.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (handler.preventDefault) {
					event.preventDefault();
				}
				if (handler.stopPropagation) {
					event.stopPropagation();
				}
				boolean right = event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT;
				if (!CancelEventTimer.cancelMouseEvent()
						&& !handler.onClickStart(event.getX(), event.getY(),
								PointerEventType.MOUSE, right)) {

					handler.onClickStart(event.getX(), event.getY(),
					        PointerEventType.MOUSE);
				}
			}
		}, MouseDownEvent.getType());

		w.addBitlessDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (handler.preventDefault) {
					event.preventDefault();
				}
				if (handler.stopPropagation) {
					event.stopPropagation();
				}
				handler.onClickStart(event.getTouches().get(0).getClientX(),
				        event.getTouches().get(0).getClientY(),
				        PointerEventType.TOUCH);
				CancelEventTimer.touchEventOccured();
			}
		}, TouchStartEvent.getType());
		
		/*
		 * w.addDomHandler(new TouchMoveHandler() { public void
		 * onTouchMove(TouchMoveEvent event) { if (handler.preventDefault) {
		 * event.preventDefault(); } if (handler.stopPropagation) {
		 * event.stopPropagation(); }
		 * handler.onClickStart(event.getTouches().get(0).getClientX(),
		 * event.getTouches().get(0).getClientY(), PointerEventType.TOUCH);
		 * CancelEventTimer.touchEventOccured(); }
		 * 
		 * }, TouchMoveEvent.getType());
		 */
	}

	/**
	 * creates the base version of a ClickEventHandler.
	 */
	public ClickStartHandler() {
	}

	/**
	 * {@link ClickStartHandler} with preventDefault and stopPropagation set
	 * explicitly. event.preventDefault() and event.stopPropagation() will also
	 * be called, if the handling-method is canceled for the event.
	 * 
	 * @param preventDefault
	 *            whether or not event.preventDefault() should be called for
	 *            MouseDownEvents and TouchStartEvents
	 * @param stopPropagation
	 *            whether or not event.stopPropagation() should be called for
	 *            MouseDownEvents and TouchStartEvents
	 */
	public ClickStartHandler(boolean preventDefault, boolean stopPropagation) {
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
	public abstract void onClickStart(int x, int y, PointerEventType type);

	/**
	 * Actual handler-method, needs to be overwritten in the instances.
	 * 
	 * @param x
	 *            x-coordinate of the event
	 * @param y
	 *            y-coordinate of the event
	 * @param type
	 *            type of the event
	 * @param right
	 *            whether it's right click
	 * @return whether it was processed
	 */
	public boolean onClickStart(int x, int y, PointerEventType type,
			boolean right) {
		return false;
	}

	/**
	 * Set preventDefault explicitly. event.preventDefault() will also be
	 * called, if the handling-method is canceled for the event.
	 * 
	 * @param preventDefault
	 *            whether or not event.preventDefault() should be called for
	 *            MouseDownEvents and TouchStartEvents
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
	 *            MouseDownEvents and TouchStartEvents
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
	 *            whether or not event.preventDefault() should be called for
	 *            MouseDownEvents and TouchStartEvents
	 * @param stopPropagation
	 *            whether or not event.stopPropagation() should be called for
	 *            MouseDownEvents and TouchStartEvents
	 */
	public static void initDefaults(Widget w, boolean preventDefault,
			boolean stopPropagation) {
		init(w, new ClickStartHandler(preventDefault, stopPropagation) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// just for preventDefault and stopPropagation.
			}
		});
	}
}
