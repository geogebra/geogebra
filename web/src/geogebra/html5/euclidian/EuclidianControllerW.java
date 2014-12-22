package geogebra.html5.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCirclePointRadius;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.Browser;
import geogebra.html5.euclidian.EuclidianPenFreehand.ShapeType;
import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.PointerEvent;
import geogebra.html5.event.ZeroOffset;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.LongTouchManager;
import geogebra.html5.gui.util.LongTouchTimer.LongTouchHandler;
import geogebra.html5.main.AppW;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class EuclidianControllerW extends EuclidianController implements MouseDownHandler, MouseUpHandler, 
MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseWheelHandler, TouchStartHandler, TouchEndHandler, 
TouchMoveHandler, TouchCancelHandler, GestureStartHandler, GestureEndHandler, GestureChangeHandler, HasOffsets, IsEuclidianController, 
LongTouchHandler {

	private long lastMoveEvent = 0;
	private PointerEvent waitingTouchMove = null;
	private PointerEvent waitingMouseMove = null;

	public EnvironmentStyleW style = new EnvironmentStyleW(); 


	/**
	 * different modes of a multitouch-event
	 */
	protected enum scaleMode {
		/**
		 * scale x-axis (two TouchStartEvents on the x-axis)
		 */
		zoomX,
		/**
		 * scale y-axis (two TouchStartEvents on the y-axis)
		 */
		zoomY,
		/**
		 * scale a circle or ellipsis with three points or an ellipsis with 5
		 * points
		 */
		circle3Points,
		/**
		 * scale a circle with 2 points
		 */
		circle2Points,
		/**
		 * scale a circle given with midpoint and a number-input as radius
		 */
		circleRadius,
		/**
		 * zooming
		 */
		view;
	}

	/**
	 * Threshold for the selection rectangle distance squared (10 pixel circle)
	 */
	public final static double SELECTION_RECT_THRESHOLD_SQR = 200.0;
	public final static double FREEHAND_MODE_THRESHOLD_SQR = 200.0;

	/**
	 * threshold for moving in case of a multitouch-event (pixel)
	 */
	public final static int MIN_MOVE = 5;

	/**
	 * the mode of the actual multitouch-event
	 */
	protected scaleMode multitouchMode;

	/**
	 * actual scale of the axes (has to be saved during multitouch)
	 */
	protected double scale;

	/**
	 * conic which's size is changed
	 */
	protected GeoConic scaleConic;

	/**
	 * midpoint of scaleConic: [0] ... x-coordinate [1] ... y-coordinate
	 */
	protected double[] midpoint;

	/**
	 * x-coordinates of the points that define scaleConic
	 */
	protected double[] originalPointX;

	/**
	 * y-coordinates of the points that define scaleConic
	 */
	protected double[] originalPointY;

	/**
	 * coordinates of the center of the multitouch-event
	 */
	protected int oldCenterX, oldCenterY;

	/**
	 * flag for blocking the scaling of the axes
	 */
	protected boolean moveAxesAllowed = true;

	private int previousMode = -1;

	private double originalRadius;

	private LongTouchManager longTouchManager;

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
	//	private boolean ignoreNextMouseEvent;

	public void moveIfWaiting(){
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
		longTouchManager = LongTouchManager.getInstance();
	}

	public void handleLongTouch(int x, int y) {
		PointerEvent event = new PointerEvent(x, y, PointerEventType.TOUCH, ZeroOffset.instance);
		event.setIsRightClick(true);
		wrapMouseReleased(event);
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
		if (targets.length() == 1 && !ignoreEvent) {
			if(time < this.lastMoveEvent + EuclidianViewW.DELAY_BETWEEN_MOVE_EVENTS){
				PointerEvent e = PointerEvent.wrapEvent(targets.get(targets.length()-1), this, event.getRelativeElement());
				boolean wasWaiting = waitingTouchMove != null || waitingMouseMove !=null;
				this.waitingTouchMove = e;
				this.waitingMouseMove = null;
				GeoGebraProfiler.moveEventsIgnored++;
				if(wasWaiting){
					this.repaintTimer.schedule(EuclidianViewW.DELAY_UNTIL_MOVE_FINISH);
				}
				return;
			}
			PointerEvent e = PointerEvent.wrapEvent(targets.get(targets.length()-1), this, event.getRelativeElement());
			if (!draggingBeyondThreshold) {
				longTouchManager.rescheduleTimerIfRunning(this, e.getX(), e.getY(), false);
			} else {
				longTouchManager.cancelTimer();
			}
			onTouchMoveNow(e, time);
		}else if (targets.length() == 2 && app.isShiftDragZoomEnabled()) {
			longTouchManager.cancelTimer();
			twoTouchMove(targets.get(0),targets.get(1));
		} else {
			longTouchManager.cancelTimer();
		}
		CancelEventTimer.touchEventOccured();
	}

	public void twoTouchMove(Touch touch, Touch touch2) {
		AbstractEvent first = PointerEvent.wrapEvent(touch,this);
		AbstractEvent second = PointerEvent.wrapEvent(touch2,this);
		this.twoTouchMove(first.getX(), first.getY(), second.getX(), second.getY());
		first.release();
		second.release();

	}



	private static double distance(final AbstractEvent t1, final AbstractEvent t2) {
		return Math.sqrt(Math.pow(t1.getX() - t2.getX(), 2)
				+ Math.pow(t1.getY() - t2.getY(), 2));
	}

	private void onTouchMoveNow(PointerEvent event,long time) {
		this.lastMoveEvent = time;
		//in SMART we actually get move events even if mouse button is up ...
		if (!DRAGMODE_MUST_BE_SELECTED) {
			wrapMouseMoved(event);
		} else {
			wrapMouseDragged(event);
		}

		this.waitingTouchMove = null;
		this.waitingMouseMove = null;
		int dragTime = (int) (System.currentTimeMillis()-time);
		GeoGebraProfiler.dragTime += dragTime;
		if(dragTime > EuclidianViewW.DELAY_UNTIL_MOVE_FINISH){
			EuclidianViewW.DELAY_UNTIL_MOVE_FINISH = dragTime + 10;
		}

		moveCounter++;
	}

	/**
	 * ignore events after first touchEnd of a multi touch event
	 */
	private boolean ignoreEvent = false;

	public void onTouchEnd(TouchEndEvent event) {
		Event.releaseCapture(event.getRelativeElement());
		DRAGMODE_MUST_BE_SELECTED = false;
		if(moveCounter  < 2){
			resetModeAfterFreehand();
		}

		this.moveIfWaiting();
		EuclidianViewW.resetDelay();
		event.stopPropagation();
		longTouchManager.cancelTimer();
		if(!comboBoxHit()){
			event.preventDefault();
		}
		if(event.getTouches().length()==0 && !ignoreEvent){
			//mouseLoc was already adjusted to the EVs coords, do not use offset again
			this.wrapMouseReleased(new PointerEvent(mouseLoc.x, mouseLoc.y, PointerEventType.TOUCH, ZeroOffset.instance));
		} else {
			// multitouch-event 
			// ignore next touchMove and touchEnd events with one touch
			ignoreEvent = true;
		}
		CancelEventTimer.touchEventOccured();

		resetModeAfterFreehand();
	}

	public void onTouchStart(TouchStartEvent event) {
		if ((app.getGuiManager() != null) && (this.getViewID() != EuclidianView.EVNO_GENERAL)) {
			// Probability calculator plot panel view should not set active toolbar ID
			// this is used by DataDisplayPanelW and PlotPanelEuclidianViewW, #plotpanelevno
			// probably both are Okay not changing the toolbar to full Graphics view toolbar
			((GuiManagerInterfaceW)app.getGuiManager()).setActiveToolbarId(App.VIEW_EUCLIDIAN);
		} else {
			setMode(EuclidianConstants.MODE_MOVE);
			//app.setMode(EuclidianConstants.MODE_MOVE);
			//app.getGuiManager().updateToolbar();
		}
		JsArray<Touch> targets = event.getTargetTouches();
		calculateEnvironment();
		if(targets.length() == 1){
			AbstractEvent e = PointerEvent.wrapEvent(targets.get(0),this);
			if (mode == EuclidianConstants.MODE_MOVE) {
				longTouchManager.scheduleTimer(this, e.getX(), e.getY());
			}
			onPointerEventStart(e);
		}
		else if(targets.length() == 2) {
			longTouchManager.cancelTimer();
			twoTouchStart(targets.get(0),targets.get(1));
		} else {
			longTouchManager.cancelTimer();
		}
		preventTouchIfNeeded(event);
		CancelEventTimer.touchEventOccured();

		prepareModeForFreehand();
		moveCounter = 0;
		ignoreEvent = false;
	}

	public void preventTouchIfNeeded(TouchStartEvent event) {
		if((!isTextfieldHasFocus())&&(!comboBoxHit())){
			event.preventDefault();
		}
	}



	public void twoTouchStart(Touch touch, Touch touch2) {
		calculateEnvironment();
		AbstractEvent first = PointerEvent.wrapEvent(touch,this);
		AbstractEvent second = PointerEvent.wrapEvent(touch2,this);
		this.twoTouchStart(first.getX(), first.getY(), second.getX(), second.getY());
		first.release();
		second.release();
	}




	private static boolean DRAGMODE_MUST_BE_SELECTED = false;
	private int deltaSum = 0;
	private int moveCounter = 0;

	public void onMouseWheel(MouseWheelEvent event) {
		//don't want to roll the scrollbar
		double delta = event.getDeltaY();
		//we are on device where many small scrolls come, we want to merge them
		int x = mouseEventX(event.getClientX() -style.getxOffset());
		int y = mouseEventX(event.getClientY() -style.getyOffset());
		if(delta==0){
			deltaSum += getNativeDelta(event.getNativeEvent());
			if(Math.abs(deltaSum)>40){
				double ds = deltaSum;
				deltaSum = 0;
				wrapMouseWheelMoved(x,y,ds,
						event.isShiftKeyDown() || event.isMetaKeyDown(), event.isAltKeyDown());
			}
			//normal scrolling
		}else{
			deltaSum=0;
			wrapMouseWheelMoved(x,y,delta,
					event.isShiftKeyDown() || event.isMetaKeyDown(), event.isAltKeyDown());
		}
		event.preventDefault(); 
	}

	private native double getNativeDelta(NativeEvent evt) /*-{
	    return -evt.wheelDelta;
    }-*/;



	public void onMouseOver(MouseOverEvent event) {
		wrapMouseEntered();
	}

	public void onMouseOut(MouseOutEvent event) {
		// cancel repaint to avoid closing newly opened tooltips
		repaintTimer.cancel();
		// hide dialogs if they are open
		int x = event.getClientX() + Window.getScrollLeft();
		int y = event.getClientY() + Window.getScrollTop(); // why scrollLeft & scrollTop; see ticket #4049

		int ex = ((EuclidianViewW) view).getAbsoluteLeft();
		int ey = ((EuclidianViewW) view).getAbsoluteTop();
		int eWidth = ((EuclidianViewW) view).getWidth();
		int eHeight = ((EuclidianViewW) view).getHeight();
		if ((x < ex || x > ex + eWidth) || (y < ey || y > ey + eHeight)) {
			ToolTipManagerW.sharedInstance().hideToolTip();
		}
		((EuclidianViewW) view).resetMsZoomer();
		AbstractEvent e = PointerEvent.wrapEvent(event,this);
		wrapMouseExited(e);
		e.release();
	}


	public void onMouseMove(MouseMoveEvent event) {
		if(CancelEventTimer.cancelMouseEvent()){
			return;
		}

		if(isExternalHandling()){
			return;
		}

		PointerEvent e = PointerEvent.wrapEvent(event, this);
		event.preventDefault();
		GeoGebraProfiler.drags++;
		long time = System.currentTimeMillis();


		if(time < this.lastMoveEvent + EuclidianViewW.DELAY_BETWEEN_MOVE_EVENTS){
			boolean wasWaiting = waitingTouchMove != null || waitingMouseMove !=null;
			this.waitingMouseMove = e;
			this.waitingTouchMove = null;
			GeoGebraProfiler.moveEventsIgnored++;
			if(wasWaiting){
				this.repaintTimer.schedule(EuclidianViewW.DELAY_UNTIL_MOVE_FINISH);
			}
			return;
		}

		onMouseMoveNow(e,time);
	}

	public void onMouseMoveNow(PointerEvent event,long time) {
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
		if(dragTime > EuclidianViewW.DELAY_UNTIL_MOVE_FINISH){
			EuclidianViewW.DELAY_UNTIL_MOVE_FINISH = dragTime + 10;
		}

		moveCounter++;
	}

	public void onMouseUp(MouseUpEvent event) {

		if(CancelEventTimer.cancelMouseEvent()){
			return;
		}

		Event.releaseCapture(event.getRelativeElement());

		if(moveCounter  < 2){
			resetModeAfterFreehand();
		}

		event.preventDefault();

		AbstractEvent e = PointerEvent.wrapEvent(event,this);
		this.moveIfWaiting();
		EuclidianViewW.resetDelay();
		DRAGMODE_MUST_BE_SELECTED = false;


		//hide dialogs if they are open
		if (app.getGuiManager() != null)
			((GuiManagerInterfaceW)app.getGuiManager()).removePopup();

		wrapMouseReleased(e);
		e.release();

		resetModeAfterFreehand();
	}




	public void onMouseDown(MouseDownEvent event) {
		deltaSum = 0;

		if(CancelEventTimer.cancelMouseEvent()){
			return;
		}
		if((!isTextfieldHasFocus())&&(!comboBoxHit())){
			event.preventDefault();
		}
		AbstractEvent e = PointerEvent.wrapEvent(event, this);
		onPointerEventStart(e);

		if (!e.isRightClick()) {
			prepareModeForFreehand();
		}
		moveCounter = 0;
		ignoreEvent = false;
	}

	public void onPointerEventStart(AbstractEvent event){
		if ((app.getGuiManager() != null) && (this.getViewID() != EuclidianView.EVNO_GENERAL)) {
			// Probability calculator plot panel view should not set active toolbar ID
			// this is used by DataDisplayPanelW and PlotPanelEuclidianViewW, #plotpanelevno
			// probably both are Okay not changing the toolbar to full Graphics view toolbar 
			((GuiManagerInterfaceW)app.getGuiManager()).setActiveToolbarId(App.VIEW_EUCLIDIAN);
		} else {
			setMode(EuclidianConstants.MODE_MOVE);
			//app.setMode(EuclidianConstants.MODE_MOVE);
			//app.getGuiManager().updateToolbar();
		}
		if ((!AutoCompleteTextFieldW.showSymbolButtonFocused)&&(!isTextfieldHasFocus())){
			DRAGMODE_MUST_BE_SELECTED = true;
		}

		wrapMousePressed(event);
		//hide PopUp if no hits was found.
		if (view.getHits().isEmpty() && this.view.hasStyleBar()) {
			this.view.getStyleBar().hidePopups();
		}
		event.release();
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
	private boolean comboboxFocused;

	public LinkedList<PointerEvent> getTouchEventPool() {
		return touchPool;
	}

	@Override
	protected boolean textfieldJustFocusedW(int x, int y, PointerEventType type) { 
		return view.textfieldClicked(x, y, type) || isComboboxFocused();
	}

	public boolean isComboboxFocused(){
		return this.comboboxFocused;
	}

	public void setComboboxFocused(boolean flag){
		this.comboboxFocused = flag;
	}


	public int touchEventX(int clientX) {
		if(((AppW)app).getLAF()!= null && ((AppW)app).getLAF().isSmart()){
			return mouseEventX(clientX - style.getxOffset());
		}
		//IE touch events are mouse events
		return Browser.supportsPointerEvents() ? mouseEventX(clientX) : mouseEventX(clientX - style.getxOffset());
	}

	public int touchEventY(int clientY) {
		if(((AppW)app).getLAF()!=null && ((AppW)app).getLAF().isSmart()){
			return mouseEventY(clientY - style.getyOffset());
		}
		//IE touch events are mouse events
		return Browser.supportsPointerEvents() ? mouseEventY(clientY) : mouseEventY(clientY - style.getyOffset());
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
		return Math.round((clientX)  *
				(1 / style.getScaleX()) *
				(1 / style.getHeightScale()));
	}

	public int mouseEventY(int clientY) {
		return Math.round((clientY)  *
				(1 / style.getScaleY()) *
				(1 / style.getHeightScale()));
	}



	public int getEvID() {
		return view.getViewID();
	}

	@Override
	public void twoTouchMove(double x1d, double y1d, double x2d, double y2d) {
		int x1 = (int) x1d;
		int x2 = (int) x2d;
		int y1 = (int) y1d;
		int y2 = (int) y2d;

		if((x1 == x2 && y1 == y2) || this.oldDistance == 0){
			return;
		}

		switch (this.multitouchMode) {
		case zoomY:
			if(this.scale == 0){
				return;
			}
			double newRatioY = this.scale * (y1 - y2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
					this.view.getYZero(), this.view.getXscale(), newRatioY);
			break;
		case zoomX:
			if(this.scale == 0){
				return;
			}
			double newRatioX = this.scale * (x1 - x2) / this.oldDistance;
			this.view.setCoordSystem(this.view.getXZero(),
					this.view.getYZero(), newRatioX, this.view.getYscale());
			break;
		case circle3Points:
			double dist = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist / this.oldDistance;
			int i = 0;

			for (GeoPointND p : scaleConic.getFreeInputPoints(this.view)) {
				double newX = midpoint[0] + (originalPointX[i] - midpoint[0])
						* scale;
				double newY = midpoint[1] + (originalPointY[i] - midpoint[1])
						* scale;
				p.setCoords(newX, newY, 1.0);
				p.updateCascade();
				i++;
			}
			kernel.notifyRepaint();
			break;
		case circle2Points:
			double dist2P = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist2P / this.oldDistance;

			// index 0 is the midpoint, index 1 is the point on the circle
			GeoPointND p = scaleConic.getFreeInputPoints(this.view).get(1);
			double newX = midpoint[0] + (originalPointX[1] - midpoint[0])
					* scale;
			double newY = midpoint[1] + (originalPointY[1] - midpoint[1])
					* scale;
			p.setCoords(newX, newY, 1.0);
			p.updateCascade();
			kernel.notifyRepaint();
			break;
		case circleRadius:
			double distR = MyMath.length(x1 - x2, y1 - y2);
			this.scale = distR / this.oldDistance;

			GeoPoint center = (GeoPoint) this.scaleConic.getParentAlgorithm().input[0];
			GeoNumeric newRadius = new GeoNumeric(
					this.kernel.getConstruction(), this.scale
					* this.originalRadius);

			scaleConic.setParentAlgorithm(new AlgoCirclePointRadius(this.kernel
					.getConstruction(), center, newRadius));
			scaleConic.setCircle(center, newRadius.getDouble());
			scaleConic.updateCascade();
			kernel.notifyUpdate(scaleConic);
			this.kernel.notifyRepaint();
			break;
		default:
			// pinch
			super.twoTouchMove(x1, y1, x2, y2);

			int centerX = (x1 + x2) / 2;
			int centerY = (y1 + y2) / 2;

			if (MyMath.length(oldCenterX - centerX, oldCenterY - centerY) > MIN_MOVE) {
				view.rememberOrigins();
				view.translateCoordSystemInPixels(centerX - oldCenterX, centerY
						- oldCenterY, 0, EuclidianConstants.MODE_TRANSLATEVIEW);

				oldCenterX = centerX;
				oldCenterY = centerY;
			}
		}
	}

	@Override
	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		this.scaleConic = null;

		view.setHits(new GPoint((int) x1, (int) y1), PointerEventType.TOUCH);
		// needs to be copied, because the reference is changed in the next step
		Hits hits1 = new Hits();
		for (GeoElement geo : view.getHits()) {
			hits1.add(geo);
		}

		view.setHits(new GPoint((int) x2, (int) y2), PointerEventType.TOUCH);
		Hits hits2 = view.getHits();

		oldCenterX = (int) (x1 + x2) / 2;
		oldCenterY = (int) (y1 + y2) / 2;

		if (hits1.hasYAxis() && hits2.hasYAxis()) {
			this.multitouchMode = scaleMode.zoomY;
			this.oldDistance = y1 - y2;
			this.scale = this.view.getYscale();
		} else if (hits1.hasXAxis() && hits2.hasXAxis()) {
			this.multitouchMode = scaleMode.zoomX;
			this.oldDistance = x1 - x2;
			this.scale = this.view.getXscale();
		} else if (hits1.size() > 0
				&& hits2.size() > 0
				&& hits1.get(0) == hits2.get(0)
				&& hits1.get(0) instanceof GeoConic
				// isClosedPath: true for circle and ellipse
				&& ((GeoConic) hits1.get(0)).isClosedPath()
				&& ((((GeoConic) hits1.get(0)).getFreeInputPoints(this.view) != null && ((GeoConic) hits1
						.get(0)).getFreeInputPoints(this.view).size() >= 2)
						|| (hits1.get(0).getParentAlgorithm() != null && hits1
						.get(0).getParentAlgorithm().input[1]
								.isIndependent()) || (hits1.get(0)
										.getParentAlgorithm() != null && !hits1.get(0)
										.getParentAlgorithm().input[1].labelSet))) {
			this.scaleConic = (GeoConic) hits1.get(0);
			// TODO: select scaleConic

			if (((GeoConic) hits1.get(0)).getFreeInputPoints(this.view).size() >= 3) {
				this.multitouchMode = scaleMode.circle3Points;
			} else if (((GeoConic) hits1.get(0)).getFreeInputPoints(this.view)
					.size() == 2) {
				this.multitouchMode = scaleMode.circle2Points;
			} else {
				this.multitouchMode = scaleMode.circleRadius;
				AlgoElement algo = scaleConic.getParentAlgorithm();
				NumberValue radius = (NumberValue) algo.input[1];
				this.originalRadius = radius.getDouble();
			}
			super.twoTouchStart(x1, y1, x2, y2);

			midpoint = new double[] { scaleConic.getMidpoint().getX(),
					scaleConic.getMidpoint().getY() };

			ArrayList<GeoPointND> points = scaleConic
					.getFreeInputPoints(this.view);
			this.originalPointX = new double[points.size()];
			this.originalPointY = new double[points.size()];
			for (int i = 0; i < points.size(); i++) {
				this.originalPointX[i] = points.get(i).getCoords().getX();
				this.originalPointY[i] = points.get(i).getCoords().getY();
			}
		} else {
			this.clearSelections();
			this.multitouchMode = scaleMode.view;
			super.twoTouchStart(x1, y1, x2, y2);
		}
	}

	/**
	 * position of last mouseDown or touchStart
	 */
	protected GPoint startPosition;

	@Override
	protected void switchModeForMousePressed(AbstractEvent e) {
		startPosition = new GPoint(e.getX(), e.getY());

		super.switchModeForMousePressed(e);

		if (this.selPoints() == 0
				&& (this.mode == EuclidianConstants.MODE_JOIN
				|| this.mode == EuclidianConstants.MODE_SEGMENT
				|| this.mode == EuclidianConstants.MODE_RAY
				|| this.mode == EuclidianConstants.MODE_VECTOR
				|| this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
				|| this.mode == EuclidianConstants.MODE_SEMICIRCLE || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {

			this.mouseLoc = new GPoint(e.getX(), e.getY());
			this.view.setHits(this.mouseLoc, e.getType());

			super.wrapMouseReleased(e);
			e.release();

			if (this.mode == EuclidianConstants.MODE_REGULAR_POLYGON
					&& this.view.getPreviewDrawable() == null) {
				this.view.setPreview(view.createPreviewSegment(selectedPoints));
			}

			this.updatePreview();
			this.view.updatePreviewableForProcessMode();
		}
	}

	@Override
	protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complex) {
		boolean newPointCreated = super.createNewPoint(hits, onPathPossible,
				inRegionPossible, intersectPossible, doSingleHighlighting,
				complex);

		GeoElement point = this.view.getHits().getFirstHit(Test.GEOPOINT);
		if (!newPointCreated
				&& this.selPoints() == 1
				&& (this.mode == EuclidianConstants.MODE_JOIN
				|| this.mode == EuclidianConstants.MODE_SEGMENT
				|| this.mode == EuclidianConstants.MODE_RAY
				|| this.mode == EuclidianConstants.MODE_VECTOR
				|| this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
				|| this.mode == EuclidianConstants.MODE_SEMICIRCLE || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {
			handleMovedElement(point, false, PointerEventType.MOUSE);
		}

		return newPointCreated;
	}

	@Override
	protected void wrapMouseDragged(AbstractEvent event) {
		if (pen != null && !penDragged && freehandModePrepared) {
			getPen().handleMouseDraggedForPenMode(event);
		}
		if (!shouldCancelDrag()) {
			if (shouldSetToFreehandMode())  {
				setModeToFreehand();
			}
			// Set capture events only if the mouse is actually down, 
			// because we need to release the capture on mouse up.
			if (waitingMouseMove == null && waitingTouchMove == null) {
				Event.setCapture(((PointerEvent) event).getRelativeElement());
			}
			super.wrapMouseDragged(event);
		}
		if (movedGeoPoint != null
				&& (this.mode == EuclidianConstants.MODE_JOIN
				|| this.mode == EuclidianConstants.MODE_SEGMENT
				|| this.mode == EuclidianConstants.MODE_RAY
				|| this.mode == EuclidianConstants.MODE_VECTOR
				|| this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
				|| this.mode == EuclidianConstants.MODE_SEMICIRCLE || this.mode == EuclidianConstants.MODE_REGULAR_POLYGON)) {
			// nothing was dragged
			super.wrapMouseMoved(event);
		}

		if (view.getPreviewDrawable() != null
				&& event.getType() == PointerEventType.TOUCH) {
			this.view.updatePreviewableForProcessMode();
		}
	}

	/**
	 * selects a GeoElement; no effect, if it is already selected
	 * 
	 * @param geo
	 *            the GeoElement to be selected
	 */
	public void select(GeoElement geo) {
		if (geo != null && !selectedGeos.contains(geo)) {
			Hits h = new Hits();
			h.add(geo);
			addSelectedGeo(h, 1, false);
		}
	}

	@Override
	public void wrapMouseReleased(AbstractEvent event) {
		// will be reset in wrapMouseReleased
		GeoPointND p = this.selPoints() == 1 ? selectedPoints.get(0) : null;

		if (this.mode == EuclidianConstants.MODE_JOIN
				|| this.mode == EuclidianConstants.MODE_SEGMENT
				|| this.mode == EuclidianConstants.MODE_RAY
				|| this.mode == EuclidianConstants.MODE_VECTOR
				|| this.mode == EuclidianConstants.MODE_CIRCLE_TWO_POINTS
				|| this.mode == EuclidianConstants.MODE_SEMICIRCLE
				|| this.mode == EuclidianConstants.MODE_REGULAR_POLYGON) {

			if (getDistance(startPosition,
					new GPoint(event.getX(), event.getY())) < this.app
					.getCapturingThreshold(event.getType())) {

				this.view.setHits(new GPoint(event.getX(), event.getY()),
						event.getType());

				if (this.selPoints() == 1 && !view.getHits().contains(p)) {
					super.wrapMouseReleased(event);
				}

				return;
			}

			super.wrapMouseReleased(event);

			this.view.setHits(new GPoint(event.getX(), event.getY()),
					event.getType());
			Hits hits = view.getHits();

			if (p != null && hits.getFirstHit(Test.GEOPOINTND) == null) {
				if (!selectedPoints.contains(p)) {
					this.selectedPoints.add(p);
				}
				createNewPointForModeOther(hits);
				this.view.setHits(new GPoint(event.getX(), event.getY()),
						event.getType());
				hits = view.getHits();
				switchModeForProcessMode(hits, event.isControlDown(), null);
			}
		} else {
			super.wrapMouseReleased(event);
		}
	}

	@Override
	protected boolean moveAxesPossible() {
		return super.moveAxesPossible() && this.moveAxesAllowed;
	}

	private boolean freehandModePrepared = false;
	private boolean freehandModeSet = false;

	protected void prepareModeForFreehand() {
		if (selectedPoints.size() != 0) {
			// make sure to switch only for the first point
			return;
		}

		// defined at the beginning, because it is modified for some modes
		GeoPoint point = (GeoPoint) this.view.getHits().getFirstHit(
				Test.GEOPOINT);
		if (point == null && this.movedGeoPoint instanceof GeoPoint) {
			point = (GeoPoint) this.movedGeoPoint;
		}

		if (this.mode == EuclidianConstants.MODE_CIRCLE_THREE_POINTS) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.circle);

			// the point will be deleted if no circle can be built, therefore
			// make sure that only a newly created point is set
			point = (this.pointCreated != null)
					&& movedGeoPoint instanceof GeoPoint ? (GeoPoint) movedGeoPoint
							: null;
		} else if (this.mode == EuclidianConstants.MODE_POLYGON) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.polygon);
		} else if (this.mode == EuclidianConstants.MODE_RIGID_POLYGON) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.rigidPolygon);
		} else if (this.mode == EuclidianConstants.MODE_VECTOR_POLYGON) {
			this.pen = new EuclidianPenFreehand(app, view);
			((EuclidianPenFreehand) pen).setExpected(ShapeType.vectorPolygon);
		} else {
			return;
		}
		freehandModePrepared = true;
		((EuclidianPenFreehand) pen).setInitialPoint(point, point != null && point.equals(pointCreated));
	}

	/**
	 * sets the mode to freehand_shape with an expected shape depending on the
	 * actual mode (has no effect if no mode is set that can be turned into
	 * freehand_shape)
	 * 
	 * For some modes requires that view.setHits(...) has been called with the
	 * correct parameters or movedGeoPoint is set correct in order to use other
	 * GeoPoints (e.g. as the first point of a polygon). Also pointCreated needs
	 * to be set correctly.
	 * 
	 */
	protected void setModeToFreehand() {
		// only executed if one of the specified modes is set
		this.previousMode = this.mode;
		this.mode = EuclidianConstants.MODE_FREEHAND_SHAPE;
		moveMode = MOVE_NONE;
		freehandModeSet = true;
	}

	/**
	 * rest all the settings that have been changed in setModeToFreehand().
	 * 
	 * no effect if setModeToFreehand() has not been called or had no effect
	 * (e.g. because the selected tool is not supported)
	 */
	protected void resetModeAfterFreehand() {
		if (freehandModePrepared) {
			freehandModePrepared = false;
			pen = null;
		}
		if (freehandModeSet) {
			freehandModeSet = false;
			this.mode = previousMode;
			moveMode = MOVE_NONE;
			view.setPreview(switchPreviewableForInitNewMode(this.mode));
			pen = null;
			this.previousMode = -1;
			this.view.repaint();
		}
	}

	private static double getDistance(GPoint p, GPoint q) {
		if (p == null || q == null) {
			return 0;
		}
		return Math.sqrt((p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y));
	}

	@Override
	protected boolean processZoomRectangle() {
		boolean processed = super.processZoomRectangle();
		if (processed) {
			selectionStartPoint.setLocation(mouseLoc);
		}
		return processed;
	}

	@Override
	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (!shouldUpdateSelectionRectangle()) {
			return;
		}
		super.updateSelectionRectangle(keepScreenRatio);
	}

	/**
	 * @return true if there is a selection rectangle, or the rectangle is bigger than a threshold.
	 */
	private boolean shouldUpdateSelectionRectangle() {
		if (view.getSelectionRectangle() != null) {
			return true;
		}
		int dx = mouseLoc.x - selectionStartPoint.x;
		int dy = mouseLoc.y - selectionStartPoint.y;
		double distSqr = (dx * dx) + (dy * dy);
		return distSqr > SELECTION_RECT_THRESHOLD_SQR;
	}
	private boolean shouldSetToFreehandMode() {
		return (isDraggingBeyondThreshold() && pen != null && !penMode(mode) && freehandModePrepared);
	}
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1, 
			Hits hits) { 
		ArrayList<GeoElement> geos = selectedGeos1 != null &&
				selectedGeos1.isEmpty() ? getAppSelectedGeos(): selectedGeos1;	
		app.getGuiManager().showPopupMenu(geos, view, mouseLoc); 
	} 

	@Override
	protected boolean freehandModePrepared() {
	    return freehandModePrepared;
	}
}
