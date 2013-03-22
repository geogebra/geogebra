package geogebra.touch.gui.euclidian;

import geogebra.common.main.App;
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
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;

public class TouchEventController implements TouchStartHandler, TouchMoveHandler, TouchEndHandler, MouseDownHandler, MouseUpHandler,
    MouseMoveHandler, MouseWheelHandler
{
	private TouchController mc;
	private double oldDistance;
	private static final double MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM = 10;

	public TouchEventController(TouchController mc)
	{
		this.mc = mc;
	}

	@Override
	public void onTouchStart(com.google.gwt.event.dom.client.TouchStartEvent event)
	{
		if (event.getTouches().length() == 1)
		{
			event.preventDefault();
			this.mc.onTouchStart(event.getTouches().get(0).getPageX(), event.getTouches().get(0).getPageY());
		}
		else if (event.getTouches().length() == 2)
		{
			this.oldDistance = distance(event.getTouches().get(0),event.getTouches().get(1));
		}
	}

	private static double distance(Touch t1, Touch t2) {
		return Math.sqrt(Math.pow(t1.getPageX() - t2.getPageX(), 2) + Math.pow(t1.getPageY() - t2.getPageY(), 2));
	}

	@Override
	public void onTouchMove(com.google.gwt.event.dom.client.TouchMoveEvent event)
	{
		event.preventDefault();

		if (event.getTouches().length() == 1)
		{
			// proceed normally
			this.mc.onTouchMove(event.getTouches().get(0).getPageX(), event.getTouches().get(0).getPageY());
		}
		else if (event.getTouches().length() == 2)
		{
			Touch first, second;
			int centerX, centerY;
			double newDistance;

			first = event.getTouches().get(0);
			second = event.getTouches().get(1);

			centerX = (first.getPageX() + second.getPageX()) / 2;
			centerY = (first.getPageY() + second.getPageY()) / 2;

			if (this.oldDistance > 0)
			{
				newDistance = distance(first,second);

				if (Math.abs(newDistance - this.oldDistance) > MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM)
				{
					//App.debug("Zooming ... "+oldDistance+":"+newDistance);
					this.mc.onPinch(centerX, centerY, newDistance / this.oldDistance);
					this.oldDistance = newDistance;
				}
			}
		}
	}

	@Override
	public void onTouchEnd(com.google.gwt.event.dom.client.TouchEndEvent event)
	{
		event.preventDefault();
		this.mc.onTouchEnd(event.getChangedTouches().get(0).getPageX(), event.getChangedTouches().get(0).getPageY());

	}

	// Listeners for Desktop
	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		event.preventDefault();
		this.mc.onTouchStart(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		this.mc.onTouchMove(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		event.preventDefault();
		this.mc.onTouchEnd(event.getClientX(), event.getClientY());
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event)
	{
		int scale = event.getDeltaY();

		this.mc.onPinch(event.getClientX(), event.getClientY(), scale);
	}

}
