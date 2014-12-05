package geogebra.html5.gui.util;

import geogebra.common.euclidian.event.PointerEventType;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class ClickMoveHandler {

	public static void init(Widget w, final ClickMoveHandler handler) {
		w.addDomHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (!CancelEventTimer.cancelMouseEvent()) {
					handler.onClickMove(event.getX(), event.getY(),
					        PointerEventType.MOUSE);
				}
			}
		}, MouseMoveEvent.getType());

		w.addDomHandler(new TouchMoveHandler() {
			public void onTouchMove(TouchMoveEvent event) {
				handler.onClickMove(event.getTouches().get(0).getClientX(),
				        event.getTouches().get(0).getClientY(),
				        PointerEventType.TOUCH);
				CancelEventTimer.touchEventOccured();
			}
		}, TouchMoveEvent.getType());
	}

	public ClickMoveHandler() {
	}

	public abstract void onClickMove(int x, int y, PointerEventType type);
}
