package geogebra.web.euclidian;


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

import geogebra.common.euclidian.AbstractEuclidianView;
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
	
	public void setApplication(AbstractApplication app) {
		this.app = (Application)app;
	}
	
	public  void setView(AbstractEuclidianView view) {
		this.view = view;
	}

	@Override
    public boolean isAltDown() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return false;
    }

	@Override
    public void setLineEndPoint(geogebra.common.awt.Point2D endPoint) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	@Override
    public GeoElement getRecordObject() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public void setMode(int mode) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onGestureChange(GestureChangeEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onGestureEnd(GestureEndEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onGestureStart(GestureStartEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onTouchCancel(TouchCancelEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onTouchMove(TouchMoveEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onTouchEnd(TouchEndEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onTouchStart(TouchStartEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onDoubleClick(DoubleClickEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onClick(ClickEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onMouseWheel(MouseWheelEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onMouseOver(MouseOverEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onMouseOut(MouseOutEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onMouseMove(MouseMoveEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onMouseUp(MouseUpEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
    }

	public void onMouseDown(MouseDownEvent event) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    
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
