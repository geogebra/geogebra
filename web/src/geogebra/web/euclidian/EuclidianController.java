package geogebra.web.euclidian;


import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

public class EuclidianController extends geogebra.common.euclidian.AbstractEuclidianController implements MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseWheelHandler, ClickHandler, DoubleClickHandler, TouchStartHandler, TouchEndHandler, TouchMoveHandler, TouchCancelHandler, GestureStartHandler, GestureEndHandler, GestureChangeHandler {

	public EuclidianController(Kernel kernel) {
		setKernel(kernel);
		setApplication((Application) kernel.getApplication());
		
		tempNum = new MyDouble(kernel);
    }
	
	@Override
	protected void setMouseLocation(AbstractEvent event) {
		mouseLoc = event.getPoint();
		
		setAltDown(event.isAltDown());
		
		mouseLoc.x = mouseLoc.x - ((EuclidianView) view).getAbsoluteLeft() + Window.getScrollLeft();
		mouseLoc.y = mouseLoc.y - ((EuclidianView) view).getAbsoluteTop() + Window.getScrollTop();
	
		if (mouseLoc.x < 0) {
			mouseLoc.x = 0;
		} else if (mouseLoc.x > view.getViewWidth()) {
			mouseLoc.x = view.getViewWidth();
		}
		if (mouseLoc.y < 0) {
			mouseLoc.y = 0;
		} else if (mouseLoc.y > view.getViewHeight()) {
			mouseLoc.y = view.getViewHeight();
		}
	}
	
	public void setApplication(AbstractApplication app) {
		this.app = (Application)app;
	}
	
	public  void setView(AbstractEuclidianView view) {
		this.view = view;
	}

	public void onGestureChange(GestureChangeEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onGestureEnd(GestureEndEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onGestureStart(GestureStartEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onTouchCancel(TouchCancelEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		 app.console(event.getAssociatedType().getName());
	}

	public void onTouchMove(TouchMoveEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i));
			wrapMouseDragged(e);
		}
		//to not move the canvas (later some sophisticated handling must be find out)
		event.preventDefault();
		event.stopPropagation();
	}

	public void onTouchEnd(TouchEndEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			 AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i));
			 //should be substracted the event just ended, and call mouseevent for that.
			 //later :-)
		}
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i));
			wrapMousePressed(e);
		}
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}
	
	private boolean DRAGMODE_MUST_BE_SELECTED = false;

	public void onDoubleClick(DoubleClickEvent event) {
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
		 wrapMouseclicked(e);
	}

	public void onClick(ClickEvent event) {
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
		 wrapMouseclicked(e);
    }

	public void onMouseWheel(MouseWheelEvent event) {
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
		 wrapMouseWheelMoved(e);
    }

	public void onMouseOver(MouseOverEvent event) {
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
		 wrapMouseEntered(e);
    }

	public void onMouseOut(MouseOutEvent event) {
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
		wrapMouseExited(e);
	}

	public void onMouseMove(MouseMoveEvent event) {
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
		 if (!DRAGMODE_MUST_BE_SELECTED) {
			 wrapMouseMoved(e);
		 } else {
			 app.console("drag");
			 wrapMouseDragged(e);
		 }
	
	}

	public void onMouseUp(MouseUpEvent event) {
		DRAGMODE_MUST_BE_SELECTED = false;
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
		wrapMouseReleased(e);
    }

	public void onMouseDown(MouseDownEvent event) {
		DRAGMODE_MUST_BE_SELECTED = true;
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent());
	    wrapMousePressed(e);
    }

	@Override
    protected void initToolTipManager() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    protected void resetToolTipManager() {
	    // TODO Auto-generated method stub
	    
    }

}
