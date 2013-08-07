package geogebra.touch.gui.euclidian;

import geogebra.common.main.App;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
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

public class TouchEventController implements TouchStartHandler,
		TouchMoveHandler, TouchEndHandler, MouseDownHandler, MouseUpHandler,
		MouseMoveHandler, MouseWheelHandler {
	private static double distance(Touch t1, Touch t2) {
		return Math.sqrt(Math.pow(t1.getClientX() - t2.getClientX(), 2)
				+ Math.pow(t1.getClientY() - t2.getClientY(), 2));
	}

	private static int getX(Touch touch) {
		return touch.getClientX();
	}

	TouchController mc;
	private double oldDistance;

	private static final double MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM = 10;

	private final Widget offsetWidget;

	private boolean ignoreMouseEvents = false;

	public TouchEventController(TouchController mc, Widget w) {
		this.mc = mc;
		this.offsetWidget = w;
	}

	private int getY(Touch touch) {
		final int y = touch.getClientY();
		return this.offsetWidget == null ? y : y
				- TouchEntryPoint.getLookAndFeel().getAppBarHeight();
	}

	// Listeners for Desktop
	@Override
	public void onMouseDown(MouseDownEvent event) {
		// ensure textfields lose focus
		if (!TouchEventController.this.mc.isTextfieldHasFocus()) {
			event.preventDefault();
		}

		if (!TouchEventController.this.ignoreMouseEvents) {
			App.debug("Mouse down" + event.getX() + "," + event.getY());
			TouchEventController.this.mc.onTouchStart(event.getX(),
					event.getY());
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (!this.ignoreMouseEvents) {
			this.mc.onTouchMove(event.getX(), event.getY());
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (!TouchEventController.this.ignoreMouseEvents) {
			TouchEventController.this.mc.onTouchEnd(event.getX(), event.getY());
		}
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if (event.getDeltaY() > 0) {
			this.mc.onPinch(event.getX(), event.getY(), 0.9);
		} else {
			this.mc.onPinch(event.getX(), event.getY(), 1.1);
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.preventDefault();
		this.mc.onTouchEnd(getX(event.getChangedTouches().get(0)),
				this.getY(event.getChangedTouches().get(0)));
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();

		if (event.getTouches().length() == 1) {
			// proceed normally
			this.mc.onTouchMove(getX(event.getTouches().get(0)),
					this.getY(event.getTouches().get(0)));
		} else if (event.getTouches().length() == 2) {
			Touch first, second;
			int centerX, centerY;
			double newDistance;

			first = event.getTouches().get(0);
			second = event.getTouches().get(1);

			centerX = (getX(first) + getX(second)) / 2;
			centerY = (getX(first) + this.getY(second)) / 2;

			if (this.oldDistance > 0) {
				newDistance = distance(first, second);

				if (Math.abs(newDistance - this.oldDistance) > MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM) {
					// App.debug("Zooming ... "+oldDistance+":"+newDistance);
					this.mc.onPinch(centerX, centerY, newDistance
							/ this.oldDistance);
					this.oldDistance = newDistance;
				}
			}
		}
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if (event.getTouches().length() == 1) {
			// ensure textfiled loses focus
			if (!this.mc.isTextfieldHasFocus()) {
				event.preventDefault();
			}
			App.debug("Touch down" + getX(event.getTouches().get(0)) + ","
					+ this.getY(event.getTouches().get(0)));
			this.ignoreMouseEvents = true;
			this.mc.onTouchStart(getX(event.getTouches().get(0)),
					this.getY(event.getTouches().get(0)));
		} else if (event.getTouches().length() == 2) {
			this.oldDistance = distance(event.getTouches().get(0), event
					.getTouches().get(1));
		}
	}

}
