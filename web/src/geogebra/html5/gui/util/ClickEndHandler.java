package geogebra.html5.gui.util;

import geogebra.common.euclidian.event.PointerEventType;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class ClickEndHandler {

	/**
	 * Attaches a handler for MouseUpEvent and a TouchEndEvent to the widget.
	 * CancelEventTimer is used to prevent duplication of events.
	 * 
	 * @param w
	 *            : Widget that the handlers are attached to
	 * @param handler
	 *            : EventHandler (instance of this class)
	 */
	public static void init(Widget w, final ClickEndHandler handler) {
		w.addDomHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				if (!CancelEventTimer.cancelMouseEvent()) {
					handler.onClickEnd(event.getX(), event.getY(),
					        PointerEventType.MOUSE);
				}
			}
		}, MouseUpEvent.getType());

		w.addDomHandler(new TouchEndHandler() {
			public void onTouchEnd(TouchEndEvent event) {
				handler.onClickEnd(event.getTouches().get(0).getClientX(),
				        event.getTouches().get(0).getClientY(),
				        PointerEventType.TOUCH);
				CancelEventTimer.touchEventOccured();
			}
		}, TouchEndEvent.getType());
	}

	public ClickEndHandler() {
	}

	/**
	 * Actual handler-method, needs to be overwritten in the instances.
	 * 
	 * @param x
	 *            : x-coordinate of the event
	 * @param y
	 *            : y-coordinate of the event
	 * @param type
	 *            : type of the event
	 */
	public abstract void onClickEnd(int x, int y, PointerEventType type);
}
