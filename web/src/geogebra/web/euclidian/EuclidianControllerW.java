package geogebra.web.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.web.euclidian.event.HasOffsets;
import geogebra.web.euclidian.event.MouseEvent;
import geogebra.web.euclidian.event.TouchEvent;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.main.AppW;

import java.util.LinkedList;

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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class EuclidianControllerW extends geogebra.common.euclidian.EuclidianController implements MouseDownHandler, MouseUpHandler, 
MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseWheelHandler, ClickHandler, DoubleClickHandler, TouchStartHandler, TouchEndHandler, 
TouchMoveHandler, TouchCancelHandler, GestureStartHandler, GestureEndHandler, GestureChangeHandler, HasOffsets {

	private long lastMoveEvent = 0;
	private AbstractEvent waitingTouchMove = null;
	private AbstractEvent waitingMouseMove = null;
	/**
	 * @return offset to get correct getX() in mouseEvents
	 */
	public int getXoffset(){
		//return EuclidianViewXOffset;
		//the former solution doesn't update on scrolling

		return ((EuclidianViewW) view).getAbsoluteLeft() - Window.getScrollLeft();
	}
	//private int EuclidianViewXOffset;
	
	//private int EuclidianViewYOffset;
	/**
	 * @return offset to get correct getY() in mouseEvents
	 */
	public int getYoffset(){
		//return EuclidianViewYOffset;
		//the former solution doesn't update on scrolling

		return ((EuclidianViewW) view).getAbsoluteTop() - Window.getScrollTop();
	}

	private boolean EuclidianOffsetsInited = false;
	
	public boolean isOffsetsUpToDate(){
		return EuclidianOffsetsInited;
	}
	
	public void updateOffsets(){
		//EuclidianViewXOffset = ((EuclidianViewW) view).getAbsoluteLeft() + Window.getScrollLeft();
		//EuclidianViewYOffset = ((EuclidianViewW) view).getAbsoluteTop() + Window.getScrollTop();	
	}
	
	private Timer repaintTimer = new Timer() {
		@Override
		public void run() {
			moveIfWaiting();
		}
	};
	
	protected void moveIfWaiting(){
		long time = System.currentTimeMillis();
		if(this.waitingMouseMove != null){
			GeoGebraProfiler.moveEventsIgnored--;
			this.onMouseMoveNow(waitingMouseMove, time);
			return;
		}
		if(this.waitingTouchMove != null){
			GeoGebraProfiler.moveEventsIgnored--;
			this.onTouchMoveNow(waitingTouchMove, time);
		}
		
	}
	
	public EuclidianControllerW(Kernel kernel) {
		super(kernel.getApplication());
		setKernel(kernel);
		
		
		tempNum = new MyDouble(kernel);
	}
	
	public  void setView(EuclidianView view) {
		this.view = view;
	}

	public void onGestureChange(GestureChangeEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				//event.preventDefault();
				//event.stopPropagation();
	}

	public void onGestureEnd(GestureEndEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				//event.preventDefault();
				//event.stopPropagation();
	}

	public void onGestureStart(GestureStartEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				//event.preventDefault();
				//event.stopPropagation();
	}

	public void onTouchCancel(TouchCancelEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		 AppW.console(event.getAssociatedType().getName());
	}

	public void onTouchMove(TouchMoveEvent event) {
		GeoGebraProfiler.drags++;
		long time = System.currentTimeMillis();
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			 if (targets.length() == 1) {
				 event.stopPropagation();
				 event.preventDefault();
			 }
		}
		if(time < this.lastMoveEvent + EuclidianViewWeb.DELAY_BETWEEN_MOVE_EVENTS){
			AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
			this.waitingTouchMove = e;
			this.waitingMouseMove = null;
			GeoGebraProfiler.moveEventsIgnored++;
			this.repaintTimer.schedule(EuclidianViewWeb.DELAY_BETWEEN_MOVE_EVENTS);
			return;
		}
		AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(targets.length()-1),this);
		onTouchMoveNow(e, time);
	}

	private void onTouchMoveNow(AbstractEvent event,long time) {
		this.lastMoveEvent = time;
		wrapMouseDragged(event);
		
	    this.waitingTouchMove = null;
	    this.waitingMouseMove = null;
	    GeoGebraProfiler.dragTime+=System.currentTimeMillis()-time;	
    }

	public void onTouchEnd(TouchEndEvent event) {
		this.moveIfWaiting();
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			 AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i),this);
			 e.release();
			 if (targets.length() == 1) {
				 event.stopPropagation();
				 event.preventDefault();
			 }
			 //should be substracted the event just ended, and call mouseevent for that.
			 //later :-)
		}
	}

	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i),this);
			wrapMousePressed(e);
			e.release();
			 if (targets.length() == 1) {
				 event.stopPropagation();
				 event.preventDefault();
			 }
			
		}
	}
	
	private static boolean DRAGMODE_MUST_BE_SELECTED = false;

	public void onDoubleClick(DoubleClickEvent event) {
		((GuiManagerW)app.getGuiManager()).setActiveToolbarId(App.VIEW_EUCLIDIAN);
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		 wrapMouseclicked(e);
		 e.release();
	}

	public void onClick(ClickEvent event) {
		((GuiManagerW)app.getGuiManager()).setActiveToolbarId(App.VIEW_EUCLIDIAN);
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		 wrapMouseclicked(e);
		 e.release();
	}

	public void onMouseWheel(MouseWheelEvent event) {
		//don't want to roll the scrollbar
		 event.preventDefault();
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),event.getDeltaY(),this);
		 wrapMouseWheelMoved(e);
		 e.release();
	}

	public void onMouseOver(MouseOverEvent event) {
		 wrapMouseEntered();
	}

	public void onMouseOut(MouseOutEvent event) {
		//hide dialogs if they are open
		int x = event.getClientX();
		int y = event.getClientY();
		int ex = ((EuclidianViewW)view).getAbsoluteLeft();
		int ey = ((EuclidianViewW)view).getAbsoluteTop();
		int eWidth = ((EuclidianViewW)view).getWidth();
		int eHeight = ((EuclidianViewW)view).getHeight();
		if ((x < ex || x > ex + eWidth) ||
				(y < ey ||y > ey + eHeight))
				((GuiManagerW)app.getGuiManager()).removePopup();
		
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		wrapMouseExited(e);
		e.release();
	}


	public void onMouseMove(MouseMoveEvent event) {
		GeoGebraProfiler.drags++;
		long time = System.currentTimeMillis();
		event.preventDefault();
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		if(time < this.lastMoveEvent + EuclidianViewWeb.DELAY_BETWEEN_MOVE_EVENTS){
			this.waitingMouseMove = e;
			this.waitingTouchMove = null;
			GeoGebraProfiler.moveEventsIgnored++;
			this.repaintTimer.schedule(EuclidianViewWeb.DELAY_BETWEEN_MOVE_EVENTS);
			return;
		}
		
		onMouseMoveNow(e,time);
	}
	
	public void onMouseMoveNow(AbstractEvent event,long time) {
		this.lastMoveEvent = time;
		 if (!DRAGMODE_MUST_BE_SELECTED) {
			 wrapMouseMoved(event);
		 } else {
			 wrapMouseDragged(event);
		 }
		 event.release();
		 this.waitingMouseMove = null;
		 this.waitingTouchMove = null;
		 GeoGebraProfiler.dragTime+=System.currentTimeMillis()-time;
	}

	public void onMouseUp(MouseUpEvent event) {
		this.moveIfWaiting();
		DRAGMODE_MUST_BE_SELECTED = false;
		event.preventDefault();	
		
		//hide dialogs if they are open
		((GuiManagerW)app.getGuiManager()).removePopup();

		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		wrapMouseReleased(e);
		e.release();
	}

	public void onMouseDown(MouseDownEvent event) {
		((GuiManagerW)app.getGuiManager()).setActiveToolbarId(App.VIEW_EUCLIDIAN);
		if ((!AutoCompleteTextFieldW.showSymbolButtonFocused)&&(!isTextfieldHasFocus())){
			DRAGMODE_MUST_BE_SELECTED = true;
		}
			
		if(!isTextfieldHasFocus()){
			event.preventDefault();
		}
			
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		wrapMousePressed(e);
		//hide PopUp if no hits was found.
		if (view.getHits().isEmpty()) {
			if (EuclidianStyleBarW.CURRENT_POP_UP != null) {
				EuclidianStyleBarW.CURRENT_POP_UP.hide();
			}
		}
		e.release();
	}
	
	@Override
	protected void initToolTipManager() {
		App.debug("initToolTipManager: implementation needed really"); // TODO Auto-generated
		
	}

	@Override
	protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1) {
		App.debug("implementation needed for 3D"); // TODO Auto-generated
		return null;
	}

	@Override
	protected void resetToolTipManager() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean hitResetIcon() {
		return app.showResetIcon()
				&& ((mouseLoc.y < 20) && (mouseLoc.x > (view.getViewWidth() - 18)));
	}
	private LinkedList<MouseEvent> mousePool = new LinkedList<MouseEvent>();
	public LinkedList<MouseEvent> getMouseEventPool() {
	    return mousePool;
    }
	private LinkedList<TouchEvent> touchPool = new LinkedList<TouchEvent>();
	public LinkedList<TouchEvent> getTouchEventPool() {
	    return touchPool;
    }

	@Override
	protected boolean textfieldJustFocusedW(GPoint p) {
		return view.textfieldClicked(p);
	}

}
