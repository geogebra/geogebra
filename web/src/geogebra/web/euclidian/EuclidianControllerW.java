package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.euclidian.EnvironmentStyleW;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.PointerEvent;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.web.euclidian.event.ZeroOffset;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import java.util.LinkedList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class EuclidianControllerW extends geogebra.common.euclidian.EuclidianController implements MouseDownHandler, MouseUpHandler, 
MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseWheelHandler, TouchStartHandler, TouchEndHandler, 
TouchMoveHandler, TouchCancelHandler, GestureStartHandler, GestureEndHandler, GestureChangeHandler, HasOffsets {

	private long lastMoveEvent = 0;
	private AbstractEvent waitingTouchMove = null;
	private AbstractEvent waitingMouseMove = null;
	
	public EnvironmentStyleW style; 
	
	
	public EnvironmentStyleW getEnvironmentStyle () {
		return style;
	}
	
	

	/**
	 * recalculates cached styles concerning browser environment
	 */
	@Override
	public void calculateEnvironment() {
	    style = new EnvironmentStyleW();
	    style.setWidthScale(getEnvWidthScale());
	    style.setHeightScale(getEnvHeightScale());
	    style.setxOffset(getEnvXoffset());
	    style.setyOffset(getEnvYoffset());
	    style.setScaleX(((AppW) app).getArticleElement().getScaleX());
	    style.setScaleY(((AppW) app).getArticleElement().getScaleY());
	    style.setScrollLeft(Window.getScrollLeft());
	    style.setScrollTop(Window.getScrollTop());
    }



	private float getEnvWidthScale() {
		EuclidianViewW v  = (EuclidianViewW) view;
		if (v.g2p.getOffsetWidth() != 0) {
			return v.g2p.getCoordinateSpaceWidth() / v.g2p.getOffsetWidth();
		}
		return 0;
	}
	
	private float getEnvHeightScale() {
		EuclidianViewW v = (EuclidianViewW) view;
		if (v.g2p.getOffsetHeight() != 0) {
			return v.g2p.getCoordinateSpaceHeight() / v.g2p.getOffsetHeight();
		}
		return 0;
	}
	
	private int getEnvXoffset(){
		//return EuclidianViewXOffset;
		//the former solution doesn't update on scrolling
		return Math.round((((EuclidianViewW) view).getAbsoluteLeft() - Window.getScrollLeft()));
				
	}
	
	
	//private int EuclidianViewXOffset;
	
	//private int EuclidianViewYOffset;
	/**
	 * @return offset to get correct getY() in mouseEvents
	 */
	private int getEnvYoffset(){
		//return EuclidianViewYOffset;
		//the former solution doesn't update on scrolling
		return ((EuclidianViewW) view).getAbsoluteTop() - Window.getScrollTop();
	}
	
	
	

	private boolean EuclidianOffsetsInited = false;
	
	public boolean isOffsetsUpToDate(){
		return EuclidianOffsetsInited;
	}
	
	private Timer repaintTimer = new Timer() {
		@Override
		public void run() {
			moveIfWaiting();
		}
	};
	private boolean ignoreNextMouseEvent;
	
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

		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				calculateEnvironment();
			}
		});

		Window.addWindowScrollHandler(new Window.ScrollHandler() {

			public void onWindowScroll(Window.ScrollEvent event) {
				calculateEnvironment();
			}
		});

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
		 Log.debug(event.getAssociatedType().getName());
	}
	
	public void onTouchMove(TouchMoveEvent event) {
		GeoGebraProfiler.drags++;
		long time = System.currentTimeMillis();
		JsArray<Touch> targets = event.getTargetTouches();
		event.stopPropagation();
		event.preventDefault();
		if (targets.length() == 1) {
			if(time < this.lastMoveEvent + EuclidianViewWeb.DELAY_BETWEEN_MOVE_EVENTS){
				AbstractEvent e = PointerEvent.wrapEvent(targets.get(targets.length()-1),this);
				boolean wasWaiting = waitingTouchMove != null || waitingMouseMove !=null;
				this.waitingTouchMove = e;
				this.waitingMouseMove = null;
				GeoGebraProfiler.moveEventsIgnored++;
				if(wasWaiting){
					this.repaintTimer.schedule(EuclidianViewWeb.DELAY_UNTIL_MOVE_FINISH);
				}
				return;
			}
			AbstractEvent e = PointerEvent.wrapEvent(targets.get(targets.length()-1),this);
			onTouchMoveNow(e, time);
		}else if (targets.length() == 2 && app.isShiftDragZoomEnabled()) {
			AbstractEvent first = PointerEvent.wrapEvent(event.getTouches().get(0),this);
			AbstractEvent second = PointerEvent.wrapEvent(event.getTouches().get(1),this);
			this.twoTouchMove(first.getX(), first.getY(), second.getX(), second.getY());
			first.release();
			second.release();
		}
	}
	
	private static double distance(final AbstractEvent t1, final AbstractEvent t2) {
		return Math.sqrt(Math.pow(t1.getX() - t2.getX(), 2)
				+ Math.pow(t1.getY() - t2.getY(), 2));
	}

	private void onTouchMoveNow(AbstractEvent event,long time) {
		this.lastMoveEvent = time;
		wrapMouseDragged(event);
		
	    this.waitingTouchMove = null;
	    this.waitingMouseMove = null;
	    int dragTime = (int) (System.currentTimeMillis()-time);
	    GeoGebraProfiler.dragTime += dragTime;
	    if(dragTime > EuclidianViewWeb.DELAY_UNTIL_MOVE_FINISH){
	    	EuclidianViewWeb.DELAY_UNTIL_MOVE_FINISH = dragTime + 10;
	    }
	    
    }

	public void onTouchEnd(TouchEndEvent event) {
		this.ignoreNextMouseEvent = true;
		this.moveIfWaiting();
		EuclidianViewWeb.resetDelay();
		event.stopPropagation();
		if(!comboBoxHit()){
			event.preventDefault();
		}
		if(event.getTouches().length()==0){
			//mouseLoc was already adjusted to the EVs coords, do not use offset again
			this.wrapMouseReleased(new PointerEvent(mouseLoc.x, mouseLoc.y, PointerEventType.TOUCH, ZeroOffset.instance));
		}
	}

	public void onTouchStart(TouchStartEvent event) {
		this.ignoreNextMouseEvent = true;
		JsArray<Touch> targets = event.getTargetTouches();
		event.stopPropagation();
		Log.debug("TS"+targets.length());
		if(targets.length() == 1){
			AbstractEvent e = PointerEvent.wrapEvent(targets.get(0),this);
			wrapMousePressed(e);
			e.release();
		}
		else if(targets.length() == 2){
			AbstractEvent first = PointerEvent.wrapEvent(event.getTouches().get(0),this);
			AbstractEvent second = PointerEvent.wrapEvent(event.getTouches().get(1),this);
			this.twoTouchStart(first.getX(), first.getY(), second.getX(), second.getY());
			first.release();
			second.release();
		}
		if((!isTextfieldHasFocus())&&(!comboBoxHit())){
			event.preventDefault();
		}
	}
	
	private static boolean DRAGMODE_MUST_BE_SELECTED = false;


	public void onMouseWheel(MouseWheelEvent event) {
		//don't want to roll the scrollbar
		 event.preventDefault();
		 wrapMouseWheelMoved(mouseEventX(event.getClientX()),mouseEventY(event.getClientY()),event.getDeltaY(),
				 event.isShiftKeyDown() || event.isMetaKeyDown(), event.isAltKeyDown());
	}

	public void onMouseOver(MouseOverEvent event) {
		 wrapMouseEntered();
	}

	public void onMouseOut(MouseOutEvent event) {
		// hide dialogs if they are open
		int x = event.getClientX();
		int y = event.getClientY();
		int ex = ((EuclidianViewW) view).getAbsoluteLeft();
		int ey = ((EuclidianViewW) view).getAbsoluteTop();
		int eWidth = ((EuclidianViewW) view).getWidth();
		int eHeight = ((EuclidianViewW) view).getHeight();
		if ((x < ex || x > ex + eWidth) || (y < ey || y > ey + eHeight)) {
			ToolTipManagerW.sharedInstance().hideToolTip();
		}
		((EuclidianViewW) view).resetMsZoomer();
		AbstractEvent e = PointerEvent.wrapEvent(event.getNativeEvent(),this);
		wrapMouseExited(e);
		e.release();
	}


	public void onMouseMove(MouseMoveEvent event) {
		if(isExternalHandling()){
			return;
		}
		
		AbstractEvent e = PointerEvent.wrapEvent(event.getNativeEvent(),this);
		event.preventDefault();
		GeoGebraProfiler.drags++;
		long time = System.currentTimeMillis();
		
		
		if(time < this.lastMoveEvent + EuclidianViewWeb.DELAY_BETWEEN_MOVE_EVENTS){
			boolean wasWaiting = waitingTouchMove != null || waitingMouseMove !=null;
			this.waitingMouseMove = e;
			this.waitingTouchMove = null;
			GeoGebraProfiler.moveEventsIgnored++;
			if(wasWaiting){
				this.repaintTimer.schedule(EuclidianViewWeb.DELAY_UNTIL_MOVE_FINISH);
			}
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
		 int dragTime = (int) (System.currentTimeMillis()-time);
		GeoGebraProfiler.dragTime += dragTime;
		if(dragTime > EuclidianViewWeb.DELAY_UNTIL_MOVE_FINISH){
			EuclidianViewWeb.DELAY_UNTIL_MOVE_FINISH = dragTime + 10;
		}
	}

	public void onMouseUp(MouseUpEvent event) {
		if(this.ignoreNextMouseEvent){
			this.ignoreNextMouseEvent = false;
			return;
		}
		
		event.preventDefault();
		

		AbstractEvent e = PointerEvent.wrapEvent(event.getNativeEvent(),this);
		this.moveIfWaiting();
		EuclidianViewWeb.resetDelay();
		DRAGMODE_MUST_BE_SELECTED = false;
		

		//hide dialogs if they are open
		if (app.getGuiManager() != null)
			((GuiManagerW)app.getGuiManager()).removePopup();

		wrapMouseReleased(e);
		e.release();
	}

	


	public void onMouseDown(MouseDownEvent event) {
		if(this.ignoreNextMouseEvent){
			this.ignoreNextMouseEvent = false;
			return;
		}
		if((!isTextfieldHasFocus())&&(!comboBoxHit())){
			event.preventDefault();
		}
		AbstractEvent e = PointerEvent.wrapEvent(event.getNativeEvent(),this);
		if (app.getGuiManager() != null)
			((GuiManagerW)app.getGuiManager()).setActiveToolbarId(App.VIEW_EUCLIDIAN);

		if ((!AutoCompleteTextFieldW.showSymbolButtonFocused)&&(!isTextfieldHasFocus())){
			DRAGMODE_MUST_BE_SELECTED = true;
		}
		
		wrapMousePressed(e);
		//hide PopUp if no hits was found.
		if (view.getHits().isEmpty()) {
			if (EuclidianStyleBarW.CURRENT_POP_UP != null) {
				EuclidianStyleBarW.CURRENT_POP_UP.hide();
			}
		}
		e.release();
	}
	
	private boolean comboBoxHit() {
		if(view.getHits() == null){
			return false;
		}
		int i=0;
		while (i<view.getHits().size()){
			GeoElement hit = view.getHits().get(i++);
			if (hit instanceof GeoList && ((GeoList)hit).drawAsComboBox()){
				return true;
			}
		}
		return false;
    }



	@Override
	protected void initToolTipManager() {
		// set tooltip manager
		ToolTipManagerW ttm = ToolTipManagerW.sharedInstance();
		//ttm.setInitialDelay(defaultInitialDelay / 2);
		//ttm.setEnabled((AppW.getAllowToolTips());
		
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
	private LinkedList<PointerEvent> mousePool = new LinkedList<PointerEvent>();
	public LinkedList<PointerEvent> getMouseEventPool() {
	    return mousePool;
    }
	private LinkedList<PointerEvent> touchPool = new LinkedList<PointerEvent>();

	public LinkedList<PointerEvent> getTouchEventPool() {
	    return touchPool;
    }

	@Override
	protected boolean textfieldJustFocusedW(int x, int y, PointerEventType type) {
		return view.textfieldClicked(x, y, type);
	}

	public int touchEventX(int clientX) {
	   return clientX - style.getxOffset();
    }

	public int touchEventY(int clientY) {
	    return clientY - style.getyOffset();
    }

	/**
	 * @return the multiplier that must be used to multiply the native event coordinates
	 */
	public float getScaleXMultiplier() {
	    return style.getScaleXMultiplier();
    }
	
	/**
	 * @return the multiplier that must be used to multiply the native event coordinates
	 */
	public float getScaleYMultiplier() {
		return style.getScaleYMultiplier();
	}

	public int mouseEventX(int clientX) {
		 return Math.round((clientX- style.getxOffset())  *
			(1 / style.getScaleX()) *
				(1 / style.getHeightScale()));
   }

	public int mouseEventY(int clientY) {
		 return Math.round((clientY- style.getyOffset())  *
			(1 / style.getScaleY()) *
				(1 / style.getHeightScale()));
    }



	public int getEvID() {
	    return view.getEuclidianViewNo();
    }

}

