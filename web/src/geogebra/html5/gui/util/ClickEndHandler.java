package geogebra.html5.gui.util;

import geogebra.common.euclidian.event.PointerEventType;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class ClickEndHandler {

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

	public abstract void onClickEnd(int x, int y, PointerEventType type);
}
