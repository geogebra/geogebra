package org.geogebra.web.web.euclidian;

import org.geogebra.common.util.MyMath;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

public class SmartTouchHandler implements TouchStartHandler, TouchEndHandler,
		TouchMoveHandler {
	private final EuclidianControllerW ec;
	private Touch t1, t2;

	public SmartTouchHandler(EuclidianControllerW ec) {
		this.ec = ec;
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		JsArray<Touch> touches = event.getTargetTouches();
		if (touches.length() == 2) {
			event.stopPropagation();
			event.preventDefault();
			t1 = touches.get(0);
			t2 = touches.get(1);
			ec.twoTouchMove(t1, t2);

		} else if (touches.length() == 1 && t2 == null) {
			if (t1 == null) {
				// no TouchStart since the last TouchEnd
				return;
			}
			t1 = touches.get(0);
			ec.onTouchMove(event);

		} else if (touches.length() == 1) {
			Touch t3 = touches.get(0);
			if (distance(t1, t3) < distance(t2, t3)) {
				t1 = t3;
			} else {
				t2 = t3;
			}
			event.stopPropagation();
			event.preventDefault();
			ec.twoTouchMove(t1, t2);

		} else {
			ec.onTouchMove(event);
		}

	}

	private double distance(Touch touch1, Touch touch2) {
		return MyMath.length(touch1.getClientX() - touch2.getClientX(),
		        touch1.getClientY() - touch2.getClientY());
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		t1 = null;
		t2 = null;
		ec.onTouchEnd(event);

	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> touches = event.getTargetTouches();
		if (touches.length() == 2) {
			t1 = touches.get(0);
			t2 = touches.get(1);
			ec.twoTouchStart(t1, t2);
			ec.preventTouchIfNeeded(event);
		} else if (touches.length() == 1 && t1 != null) {
			t2 = touches.get(0);
			ec.twoTouchStart(t1, t2);
			ec.preventTouchIfNeeded(event);
		} else if (touches.length() == 1) {
			t1 = touches.get(0);
			// we saved the touch, proceed normally
			ec.onTouchStart(event);
		} else {
			ec.onTouchStart(event);
		}

	}
}
