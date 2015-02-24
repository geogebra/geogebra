package geogebra.html5.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoCirclePointRadius;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoSphereNDPointRadius;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.Browser;
import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.PointerEvent;
import geogebra.html5.event.ZeroOffset;
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
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class MouseTouchGestureControllerW implements
 HasOffsets {

	private AppW app;
	private EuclidianController ec;

	private long lastMoveEvent = 0;
	private PointerEvent waitingTouchMove = null;
	private PointerEvent waitingMouseMove = null;

	public EnvironmentStyleW style = new EnvironmentStyleW();

	/**
	 * different modes of a multitouch-event
	 */
	protected enum MultitouchMode {
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
		 * scale a circle given as input formula
		 */
		circleFormula,
		/**
		 * zooming
		 */
		view,
		/**
		 * move a line with two fingers
		 */
		moveLine;
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
	protected MultitouchMode multitouchMode;

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
	 * the line to move with two fingers
	 */
	private GeoLine lineToMove;
	private boolean firstTouchIsAttachedToStartPoint;
	private GeoPoint firstFingerTouch;
	private GeoPoint secondFingerTouch;

	/**
	 * flag for blocking the scaling of the axes
	 */
	protected boolean moveAxesAllowed = true;

	private int previousMode = -1;

	private double originalRadius;

	private LongTouchManager longTouchManager;

	/**
	 * whether to keep the actual tool after successfully constructing an
	 * element (if set to true) or to change back to the move tool (if set to
	 * false)
	 */
	public boolean USE_STICKY_TOOLS = true;

	private boolean actualSticky = false;

	public EnvironmentStyleW getEnvironmentStyle() {
		return style;
	}

	/**
	 * recalculates cached styles concerning browser environment
	 */
	public void calculateEnvironment() {
		if (ec.view == null) {
			return;
		}
		style = new EnvironmentStyleW();
		style.setWidthScale(getEnvWidthScale());
		style.setHeightScale(getEnvHeightScale());
		style.setxOffset(getEnvXoffset());
		style.setyOffset(getEnvYoffset());
		style.setScaleX(app.getArticleElement().getScaleX());
		style.setScaleY(app.getArticleElement().getScaleY());
		style.setScrollLeft(Window.getScrollLeft());
		style.setScrollTop(Window.getScrollTop());
	}

	private float getEnvWidthScale() {
		if (ec.view == null) {
			return 1;
		}
		EuclidianViewWInterface v = (EuclidianViewWInterface) ec.view;
		if (v.getG2P().getOffsetWidth() != 0) {
			return v.getG2P().getCoordinateSpaceWidth()
			        / v.getG2P().getOffsetWidth();
		}
		return 0;
	}

	private float getEnvHeightScale() {
		EuclidianViewWInterface v = (EuclidianViewWInterface) ec.view;
		if (v.getG2P().getOffsetHeight() != 0) {
			return v.getG2P().getCoordinateSpaceHeight()
			        / v.getG2P().getOffsetHeight();
		}
		return 0;
	}

	private int getEnvXoffset() {
		// return EuclidianViewXOffset;
		// the former solution doesn't update on scrolling
		return Math
.round((((EuclidianViewWInterface) ec.view)
		        .getAbsoluteLeft() - Window
		        .getScrollLeft()));

	}

	// private int EuclidianViewXOffset;

	// private int EuclidianViewYOffset;
	/**
	 * @return offset to get correct getY() in mouseEvents
	 */
	private int getEnvYoffset() {
		// return EuclidianViewYOffset;
		// the former solution doesn't update on scrolling
		return ((EuclidianViewWInterface) ec.view).getAbsoluteTop()
		        - Window.getScrollTop();
	}

	private boolean EuclidianOffsetsInited = false;

	public boolean isOffsetsUpToDate() {
		return EuclidianOffsetsInited;
	}

	private Timer repaintTimer = new Timer() {
		@Override
		public void run() {
			moveIfWaiting();
		}
	};

	// private boolean ignoreNextMouseEvent;

	public void moveIfWaiting() {
		long time = System.currentTimeMillis();
		if (this.waitingMouseMove != null) {
			GeoGebraProfiler.moveEventsIgnored--;
			this.onMouseMoveNow(waitingMouseMove, time, false);
			return;
		}
		if (this.waitingTouchMove != null) {
			GeoGebraProfiler.moveEventsIgnored--;
			this.onTouchMoveNow(waitingTouchMove, time, false);
		}

	}

	public MouseTouchGestureControllerW(AppW app, EuclidianController ec) {
		// super(kernel.getApplication());
		// setKernel(kernel);

		this.app = app;
		this.ec = ec;

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				calculateEnvironment();
			}
		});

		Window.addWindowScrollHandler(new Window.ScrollHandler() {

			@Override
			public void onWindowScroll(Window.ScrollEvent event) {
				calculateEnvironment();
			}
		});

		longTouchManager = LongTouchManager.getInstance();
	}

	public void handleLongTouch(int x, int y) {
		PointerEvent event = new PointerEvent(x, y, PointerEventType.TOUCH,
		        ZeroOffset.instance);
		event.setIsRightClick(true);
		ec.wrapMouseReleased(event);
	}


	public void onGestureChange(GestureChangeEvent event) {
		// AbstractEvent e =
		// geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		// to not move the canvas (later some sophisticated handling must be
		// find out)
		// event.preventDefault();
		// event.stopPropagation();
	}

	public void onGestureEnd(GestureEndEvent event) {
		// AbstractEvent e =
		// geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		// to not move the canvas (later some sophisticated handling must be
		// find out)
		// event.preventDefault();
		// event.stopPropagation();
	}

	public void onGestureStart(GestureStartEvent event) {
		// AbstractEvent e =
		// geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		// to not move the canvas (later some sophisticated handling must be
		// find out)
		// event.preventDefault();
		// event.stopPropagation();
	}

	public void onTouchCancel(TouchCancelEvent event) {
		// AbstractEvent e =
		// geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		Log.debug(event.getAssociatedType().getName());
	}

	public void onTouchMove(TouchMoveEvent event) {
		GeoGebraProfiler.drags++;
		long time = System.currentTimeMillis();
		JsArray<Touch> targets = event.getTargetTouches();
		event.stopPropagation();
		event.preventDefault();
		if (targets.length() == 1 && !ignoreEvent) {
			if (time < this.lastMoveEvent
			        + EuclidianViewW.DELAY_BETWEEN_MOVE_EVENTS) {
				PointerEvent e = PointerEvent.wrapEvent(
				        targets.get(targets.length() - 1), this,
				        event.getRelativeElement());
				boolean wasWaiting = waitingTouchMove != null
				        || waitingMouseMove != null;
				this.waitingTouchMove = e;
				this.waitingMouseMove = null;
				GeoGebraProfiler.moveEventsIgnored++;
				if (wasWaiting) {
					this.repaintTimer
					        .schedule(EuclidianViewW.DELAY_UNTIL_MOVE_FINISH);
				}
				return;
			}
			PointerEvent e = PointerEvent.wrapEvent(
			        targets.get(targets.length() - 1), this,
			        event.getRelativeElement());
			if (!ec.draggingBeyondThreshold) {
				longTouchManager.rescheduleTimerIfRunning(
				        (LongTouchHandler) ec, e.getX(),
				        e.getY(), false);
			} else {
				longTouchManager.cancelTimer();
			}
			onTouchMoveNow(e, time, true);
		} else if (targets.length() == 2 && app.isShiftDragZoomEnabled()) {
			longTouchManager.cancelTimer();
			twoTouchMove(targets.get(0), targets.get(1));
		} else {
			longTouchManager.cancelTimer();
		}
		CancelEventTimer.touchEventOccured();
	}

	public void twoTouchMove(Touch touch, Touch touch2) {
		AbstractEvent first = PointerEvent.wrapEvent(touch, this);
		AbstractEvent second = PointerEvent.wrapEvent(touch2, this);
		ec.twoTouchMove(first.getX(), first.getY(), second.getX(),
		        second.getY());
		first.release();
		second.release();

	}

	private static double distance(final AbstractEvent t1,
	        final AbstractEvent t2) {
		return Math.sqrt(Math.pow(t1.getX() - t2.getX(), 2)
		        + Math.pow(t1.getY() - t2.getY(), 2));
	}

	public void onTouchMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		this.lastMoveEvent = time;
		// in SMART we actually get move events even if mouse button is up ...
		if (!DRAGMODE_MUST_BE_SELECTED) {
			ec.wrapMouseMoved(event);
		} else {
			ec.wrapMouseDragged(event, startCapture);
		}

		this.waitingTouchMove = null;
		this.waitingMouseMove = null;
		int dragTime = (int) (System.currentTimeMillis() - time);
		GeoGebraProfiler.dragTime += dragTime;
		if (dragTime > EuclidianViewW.DELAY_UNTIL_MOVE_FINISH) {
			EuclidianViewW.DELAY_UNTIL_MOVE_FINISH = dragTime + 10;
		}

		moveCounter++;
	}

	/**
	 * ignore events after first touchEnd of a multi touch event
	 */
	private boolean ignoreEvent = false;

	public void onTouchEnd(TouchEndEvent event) {
		App.debug("RELEASE touch");
		Event.releaseCapture(event.getRelativeElement());
		DRAGMODE_MUST_BE_SELECTED = false;
		if (moveCounter < 2) {
			ec.resetModeAfterFreehand();
		}

		this.moveIfWaiting();
		EuclidianViewW.resetDelay();
		event.stopPropagation();
		longTouchManager.cancelTimer();
		if (!comboBoxHit()) {
			event.preventDefault();
		}
		if (event.getTouches().length() == 0 && !ignoreEvent) {
			// mouseLoc was already adjusted to the EVs coords, do not use
			// offset again
			ec.wrapMouseReleased(new PointerEvent(ec.mouseLoc.x,
			        ec.mouseLoc.y,
			        PointerEventType.TOUCH, ZeroOffset.instance));
		} else {
			// multitouch-event
			// ignore next touchMove and touchEnd events with one touch
			ignoreEvent = true;
		}
		CancelEventTimer.touchEventOccured();

		ec.resetModeAfterFreehand();
	}


	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		calculateEnvironment();
		if (targets.length() == 1) {
			AbstractEvent e = PointerEvent.wrapEvent(targets.get(0), this);
			if (ec.getMode() == EuclidianConstants.MODE_MOVE) {
				longTouchManager.scheduleTimer((LongTouchHandler) ec, e.getX(),
				        e.getY());
			}
			onPointerEventStart(e);
		} else if (targets.length() == 2) {
			longTouchManager.cancelTimer();
			twoTouchStart(targets.get(0), targets.get(1));
		} else {
			longTouchManager.cancelTimer();
		}
		preventTouchIfNeeded(event);
		CancelEventTimer.touchEventOccured();

		ec.prepareModeForFreehand();
		moveCounter = 0;
		ignoreEvent = false;
	}


	public void preventTouchIfNeeded(TouchStartEvent event) {
		if ((!ec.isTextfieldHasFocus()) && (!comboBoxHit())) {
			event.preventDefault();
		}
	}

	public void twoTouchStart(Touch touch, Touch touch2) {
		calculateEnvironment();
		AbstractEvent first = PointerEvent.wrapEvent(touch, this);
		AbstractEvent second = PointerEvent.wrapEvent(touch2, this);
		ec.twoTouchStart(first.getX(), first.getY(), second.getX(),
		        second.getY());
		first.release();
		second.release();
	}

	private static boolean DRAGMODE_MUST_BE_SELECTED = false;
	private int deltaSum = 0;
	private int moveCounter = 0;
	private boolean DRAGMODE_IS_RIGHT_CLICK = false;

	public void onMouseWheel(MouseWheelEvent event) {
		// don't want to roll the scrollbar
		double delta = event.getDeltaY();
		// we are on device where many small scrolls come, we want to merge them
		int x = mouseEventX(event.getClientX() - style.getxOffset());
		int y = mouseEventX(event.getClientY() - style.getyOffset());
		if (delta == 0) {
			deltaSum += getNativeDelta(event.getNativeEvent());
			if (Math.abs(deltaSum) > 40) {
				double ds = deltaSum;
				deltaSum = 0;
				ec.wrapMouseWheelMoved(x, y, ds,
				        event.isShiftKeyDown() || event.isMetaKeyDown(),
				        event.isAltKeyDown());
			}
			// normal scrolling
		} else {
			deltaSum = 0;
			ec.wrapMouseWheelMoved(x, y, delta,
			        event.isShiftKeyDown() || event.isMetaKeyDown(),
			        event.isAltKeyDown());
		}
		event.preventDefault();
	}

	private native double getNativeDelta(NativeEvent evt) /*-{
		return -evt.wheelDelta;
	}-*/;

	public void onMouseOver(MouseOverEvent event) {
		ec.wrapMouseEntered();
	}

	public void onMouseOut(MouseOutEvent event) {
		// cancel repaint to avoid closing newly opened tooltips
		repaintTimer.cancel();
		// hide dialogs if they are open
		int x = event.getClientX() + Window.getScrollLeft();
		int y = event.getClientY() + Window.getScrollTop(); // why scrollLeft &
		                                                    // scrollTop; see
		                                                    // ticket #4049

		int ex = ((EuclidianViewWInterface) ec.view).getAbsoluteLeft();
		int ey = ((EuclidianViewWInterface) ec.view).getAbsoluteTop();
		int eWidth = ((EuclidianViewWInterface) ec.view).getWidth();
		int eHeight = ((EuclidianViewWInterface) ec.view).getHeight();
		if ((x < ex || x > ex + eWidth) || (y < ey || y > ey + eHeight)) {
			ToolTipManagerW.sharedInstance().hideToolTip();
		}
		((EuclidianViewWInterface) ec.view).resetMsZoomer();
		AbstractEvent e = PointerEvent.wrapEvent(event, this);
		ec.wrapMouseExited(e);
		e.release();
	}

	public void onMouseMove(MouseMoveEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}

		if (ec.isExternalHandling()) {
			return;
		}

		PointerEvent e = PointerEvent.wrapEvent(event, this);
		event.preventDefault();
		GeoGebraProfiler.drags++;
		long time = System.currentTimeMillis();

		if (time < this.lastMoveEvent
		        + EuclidianViewW.DELAY_BETWEEN_MOVE_EVENTS) {
			boolean wasWaiting = waitingTouchMove != null
			        || waitingMouseMove != null;
			this.waitingMouseMove = e;
			this.waitingTouchMove = null;
			GeoGebraProfiler.moveEventsIgnored++;
			if (wasWaiting) {
				this.repaintTimer
				        .schedule(EuclidianViewW.DELAY_UNTIL_MOVE_FINISH);
			}
			return;
		}

		onMouseMoveNow(e, time, true);
	}

	public void onMouseMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		this.lastMoveEvent = time;
		if (!DRAGMODE_MUST_BE_SELECTED) {
			ec.wrapMouseMoved(event);
		} else {
			event.setIsRightClick(DRAGMODE_IS_RIGHT_CLICK);
			ec.wrapMouseDragged(event, startCapture);
		}
		event.release();
		this.waitingMouseMove = null;
		this.waitingTouchMove = null;
		int dragTime = (int) (System.currentTimeMillis() - time);
		GeoGebraProfiler.dragTime += dragTime;
		if (dragTime > EuclidianViewW.DELAY_UNTIL_MOVE_FINISH) {
			EuclidianViewW.DELAY_UNTIL_MOVE_FINISH = dragTime + 10;
		}

		moveCounter++;
	}

	public void onMouseUp(MouseUpEvent event) {
		App.debug("RELEASE");
		Event.releaseCapture(event.getRelativeElement());
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}



		if (moveCounter < 2) {
			ec.resetModeAfterFreehand();
		}

		event.preventDefault();

		AbstractEvent e = PointerEvent.wrapEvent(event, this);
		this.moveIfWaiting();
		EuclidianViewW.resetDelay();
		DRAGMODE_MUST_BE_SELECTED = false;

		// hide dialogs if they are open
		if (app.getGuiManager() != null)
			app.getGuiManager().removePopup();

		ec.wrapMouseReleased(e);
		e.release();

		boolean elementCreated = ec.pen != null
		        && ec.pen.getCreatedShape() != null;

		ec.resetModeAfterFreehand();

		if (elementCreated) {
			ec.toolCompleted();
		}
	}

	public void onMouseDown(MouseDownEvent event) {
		deltaSum = 0;

		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		if ((!ec.isTextfieldHasFocus()) && (!comboBoxHit())) {
			event.preventDefault();
		}
		AbstractEvent e = PointerEvent.wrapEvent(event, this);
		ec.onPointerEventStart(e);

		if (!e.isRightClick()) {
			ec.prepareModeForFreehand();
		}
		moveCounter = 0;
		ignoreEvent = false;
	}

	public void onPointerEventStart(AbstractEvent event) {
		if ((!AutoCompleteTextFieldW.showSymbolButtonFocused)
		        && (!ec.isTextfieldHasFocus())) {
			DRAGMODE_MUST_BE_SELECTED = true;
			DRAGMODE_IS_RIGHT_CLICK = event.isRightClick();
		}

		ec.wrapMousePressed(event);
		// hide PopUp if no hits was found.
		if (ec.view.getHits().isEmpty() && ec.view.hasStyleBar()) {
			ec.view.getStyleBar().hidePopups();
		}
		event.release();
	}

	private boolean comboBoxHit() {
		if (ec.view.getHits() == null) {
			return false;
		}
		int i = 0;
		while (i < ec.view.getHits().size()) {
			GeoElement hit = ec.view.getHits().get(i++);
			if (hit instanceof GeoList && ((GeoList) hit).drawAsComboBox()) {
				return true;
			}
		}
		return false;
	}

	public void initToolTipManager() {
		// set tooltip manager
		ToolTipManagerW ttm = ToolTipManagerW.sharedInstance();
		// ttm.setInitialDelay(defaultInitialDelay / 2);
		// ttm.setEnabled((AppW.getAllowToolTips());

	}

	public void resetToolTipManager() {
		// TODO Auto-generated method stub

	}

	public boolean hitResetIcon() {
		return app.showResetIcon()
		        && ((ec.mouseLoc.y < 20) && (ec.mouseLoc.x > (ec.view
		                .getViewWidth() - 18)));
	}

	private LinkedList<PointerEvent> mousePool = new LinkedList<PointerEvent>();

	@Override
	public LinkedList<PointerEvent> getMouseEventPool() {
		return mousePool;
	}

	private LinkedList<PointerEvent> touchPool = new LinkedList<PointerEvent>();
	private boolean comboboxFocused;


	@Override
	public LinkedList<PointerEvent> getTouchEventPool() {
		return touchPool;
	}

	protected boolean textfieldJustFocusedW(int x, int y, PointerEventType type) {
		return ec.view.textfieldClicked(x, y, type) || isComboboxFocused();
	}

	public boolean isComboboxFocused() {
		return this.comboboxFocused;
	}

	public void setComboboxFocused(boolean flag) {
		this.comboboxFocused = flag;
	}

	@Override
	public int touchEventX(int clientX) {
		if (app.getLAF() != null && app.getLAF().isSmart()) {
			return mouseEventX(clientX - style.getxOffset());
		}
		// IE touch events are mouse events
		return Browser.supportsPointerEvents() ? mouseEventX(clientX)
		        : mouseEventX(clientX - style.getxOffset());
	}

	@Override
	public int touchEventY(int clientY) {
		if (app.getLAF() != null && app.getLAF().isSmart()) {
			return mouseEventY(clientY - style.getyOffset());
		}
		// IE touch events are mouse events
		return Browser.supportsPointerEvents() ? mouseEventY(clientY)
		        : mouseEventY(clientY - style.getyOffset());
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public float getScaleXMultiplier() {
		return style.getScaleXMultiplier();
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public float getScaleYMultiplier() {
		return style.getScaleYMultiplier();
	}

	@Override
	public int mouseEventX(int clientX) {
		return Math.round((clientX) * (1 / style.getScaleX())
		        * (1 / style.getHeightScale()));
	}

	@Override
	public int mouseEventY(int clientY) {
		return Math.round((clientY) * (1 / style.getScaleY())
		        * (1 / style.getHeightScale()));
	}

	@Override
	public int getEvID() {
		return ec.view.getViewID();
	}

	public void twoTouchMove(double x1d, double y1d, double x2d, double y2d) {
		int x1 = (int) x1d;
		int x2 = (int) x2d;
		int y1 = (int) y1d;
		int y2 = (int) y2d;

		if ((x1 == x2 && y1 == y2) || ec.oldDistance == 0) {
			return;
		}

		switch (this.multitouchMode) {
		case zoomY:
			if (this.scale == 0 || !app.isShiftDragZoomEnabled()) {
				return;
			}
			double newRatioY = this.scale * (y1 - y2) / ec.oldDistance;
			ec.view.setCoordSystem(ec.view.getXZero(), ec.view.getYZero(),
			        ec.view.getXscale(), newRatioY);
			break;
		case zoomX:
			if (this.scale == 0 || !app.isShiftDragZoomEnabled()) {
				return;
			}
			double newRatioX = this.scale * (x1 - x2) / ec.oldDistance;
			ec.view.setCoordSystem(ec.view.getXZero(), ec.view.getYZero(),
			        newRatioX, ec.view.getYscale());
			break;
		case circle3Points:
			double dist = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist / ec.oldDistance;
			int i = 0;

			for (GeoPointND p : scaleConic.getFreeInputPoints(ec.view)) {
				double newX = midpoint[0] + (originalPointX[i] - midpoint[0])
				        * scale;
				double newY = midpoint[1] + (originalPointY[i] - midpoint[1])
				        * scale;
				p.setCoords(newX, newY, 1.0);
				p.updateCascade();
				i++;
			}
			ec.kernel.notifyRepaint();
			break;
		case circle2Points:
			double dist2P = MyMath.length(x1 - x2, y1 - y2);
			this.scale = dist2P / ec.oldDistance;

			// index 0 is the midpoint, index 1 is the point on the circle
			GeoPointND p = scaleConic.getFreeInputPoints(ec.view).get(1);
			double newX = midpoint[0] + (originalPointX[1] - midpoint[0])
			        * scale;
			double newY = midpoint[1] + (originalPointY[1] - midpoint[1])
			        * scale;
			p.setCoords(newX, newY, 1.0);
			p.updateCascade();
			ec.kernel.notifyRepaint();
			break;
		case circleRadius:
			double distR = MyMath.length(x1 - x2, y1 - y2);
			this.scale = distR / ec.oldDistance;
			GeoNumeric newRadius = new GeoNumeric(ec.kernel.getConstruction(),
			        this.scale * this.originalRadius);

			((AlgoSphereNDPointRadius) scaleConic.getParentAlgorithm())
			        .setRadius(newRadius);
			scaleConic.updateCascade();
			ec.kernel.notifyUpdate(scaleConic);
			ec.kernel.notifyRepaint();
			break;
		case circleFormula:
			double distF = MyMath.length(x1 - x2, y1 - y2);
			this.scale = distF / ec.oldDistance;

			scaleConic.halfAxes[0] = this.scale * this.originalRadius;
			scaleConic.halfAxes[1] = this.scale * this.originalRadius;
			scaleConic.updateCascade();
			ec.kernel.notifyUpdate(scaleConic);
			ec.kernel.notifyRepaint();
			break;
		case moveLine:
			// ignore minimal changes of finger-movement
			if (onlyJitter(firstFingerTouch.getX(), firstFingerTouch.getY(),
			        secondFingerTouch.getX(), secondFingerTouch.getY(), x1d,
			        y1d, x2d, y2d)) {
				return;
			}

			Coords oldStart = firstFingerTouch.getCoords();
			Coords oldEnd = secondFingerTouch.getCoords();
			if (firstTouchIsAttachedToStartPoint) {
				firstFingerTouch.setCoords(ec.view.toRealWorldCoordX(x1d),
				        ec.view.toRealWorldCoordY(y1d), 1);
				secondFingerTouch.setCoords(ec.view.toRealWorldCoordX(x2d),
				        ec.view.toRealWorldCoordY(y2d), 1);
			} else {
				secondFingerTouch.setCoords(ec.view.toRealWorldCoordX(x1d),
				        ec.view.toRealWorldCoordY(y1d), 1);
				firstFingerTouch.setCoords(ec.view.toRealWorldCoordX(x2d),
				        ec.view.toRealWorldCoordY(y2d), 1);
			}

			// set line through the two finger touches
			Coords crossP = firstFingerTouch.getCoords().crossProduct(
			        secondFingerTouch.getCoords());
			lineToMove.setCoords(crossP.getX(), crossP.getY(), crossP.getZ());
			lineToMove.updateCascade();

			// update coords of startPoint
			lineToMove.pointChanged(lineToMove.getStartPoint());
			lineToMove.getStartPoint().updateCoords();

			// update coords of endPoint
			lineToMove.pointChanged(lineToMove.getEndPoint());
			lineToMove.getEndPoint().updateCoords();

			// also move points along the line
			double newStartX = lineToMove.getStartPoint().getX()
			        - (oldStart.getX() - firstFingerTouch.getX());
			double newStartY = lineToMove.getStartPoint().getY()
			        - (oldStart.getY() - firstFingerTouch.getY());
			double newEndX = lineToMove.getEndPoint().getX()
			        - (oldEnd.getX() - secondFingerTouch.getX());
			double newEndY = lineToMove.getEndPoint().getY()
			        - (oldEnd.getY() - secondFingerTouch.getY());

			lineToMove.getStartPoint().setCoords(newStartX, newStartY, 1);
			lineToMove.getEndPoint().setCoords(newEndX, newEndY, 1);

			lineToMove.getStartPoint().updateCascade();
			lineToMove.getEndPoint().updateCascade();

			ec.kernel.notifyUpdate(lineToMove.getStartPoint());
			ec.kernel.notifyUpdate(lineToMove.getEndPoint());

			ec.kernel.notifyRepaint();

			break;
		default:
			if (!app.isShiftDragZoomEnabled()) {
				return;
			}
			// pinch
			ec.twoTouchMoveCommon(x1, y1, x2, y2);

			int centerX = (x1 + x2) / 2;
			int centerY = (y1 + y2) / 2;

			if (MyMath.length(oldCenterX - centerX, oldCenterY - centerY) > MIN_MOVE) {
				ec.view.rememberOrigins();
				ec.view.translateCoordSystemInPixels(centerX - oldCenterX,
				        centerY
				        - oldCenterY, 0, EuclidianConstants.MODE_TRANSLATEVIEW);

				oldCenterX = centerX;
				oldCenterY = centerY;
			}
		}
	}

	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		this.scaleConic = null;

		ec.view.setHits(new GPoint((int) x1, (int) y1), PointerEventType.TOUCH);
		// needs to be copied, because the reference is changed in the next step
		Hits hits1 = new Hits();
		for (GeoElement geo : ec.view.getHits()) {
			hits1.add(geo);
		}

		ec.view.setHits(new GPoint((int) x2, (int) y2), PointerEventType.TOUCH);
		Hits hits2 = ec.view.getHits();

		oldCenterX = (int) (x1 + x2) / 2;
		oldCenterY = (int) (y1 + y2) / 2;

		if (hits1.hasYAxis() && hits2.hasYAxis()) {
			this.multitouchMode = MultitouchMode.zoomY;
			ec.oldDistance = y1 - y2;
			this.scale = ec.view.getYscale();
		} else if (hits1.hasXAxis() && hits2.hasXAxis()) {
			this.multitouchMode = MultitouchMode.zoomX;
			ec.oldDistance = x1 - x2;
			this.scale = ec.view.getXscale();
		} else if (hits1.size() > 0
		        && hits2.size() > 0
		        && hits1.get(0) == hits2.get(0)
		        && hits1.get(0) instanceof GeoConic
		        // isClosedPath: true for circle and ellipse
		        && ((GeoConic) hits1.get(0)).isClosedPath()) {
			this.scaleConic = (GeoConic) hits1.get(0);
			// TODO: select scaleConic

			if (scaleConic.getFreeInputPoints(ec.view) == null
			        && scaleConic.isCircle()) {
				this.multitouchMode = MultitouchMode.circleFormula;
				this.originalRadius = scaleConic.getHalfAxis(0);
			} else if (scaleConic.getFreeInputPoints(ec.view).size() >= 3) {
				this.multitouchMode = MultitouchMode.circle3Points;
			} else if (scaleConic.getFreeInputPoints(ec.view)
			        .size() == 2) {
				this.multitouchMode = MultitouchMode.circle2Points;
			} else if (app.isPrerelease()
			        && scaleConic.getParentAlgorithm() instanceof AlgoCirclePointRadius) {
				this.multitouchMode = MultitouchMode.circleRadius;
				AlgoElement algo = scaleConic.getParentAlgorithm();
				NumberValue radius = (NumberValue) algo.input[1];
				this.originalRadius = radius.getDouble();
			} else {
				// TODO scale other conic-types (e.g. ellipses with formula)
				scaleConic = null;
				ec.clearSelections();
				this.multitouchMode = MultitouchMode.view;
				ec.twoTouchStartCommon(x1, y1, x2, y2);
				return;
			}
			ec.twoTouchStartCommon(x1, y1, x2, y2);

			midpoint = new double[] { scaleConic.getMidpoint().getX(),
			        scaleConic.getMidpoint().getY() };

			ArrayList<GeoPointND> points = scaleConic
			        .getFreeInputPoints(ec.view);
			this.originalPointX = new double[points.size()];
			this.originalPointY = new double[points.size()];
			for (int i = 0; i < points.size(); i++) {
				this.originalPointX[i] = points.get(i).getCoords().getX();
				this.originalPointY[i] = points.get(i).getCoords().getY();
			}
		} else if (hits1.size() > 0 && hits2.size() > 0
		        && hits1.get(0) == hits2.get(0)
		        && hits1.get(0) instanceof GeoLine
		        && isMovableWithTwoFingers(hits1.get(0))) {
			this.multitouchMode = MultitouchMode.moveLine;
			lineToMove = (GeoLine) hits1.get(0);

			GeoPoint touch1 = new GeoPoint(ec.kernel.getConstruction(),
			        ec.view.toRealWorldCoordX(x1),
			        ec.view.toRealWorldCoordY(y1), 1);
			GeoPoint touch2 = new GeoPoint(ec.kernel.getConstruction(),
			        ec.view.toRealWorldCoordX(x2),
			        ec.view.toRealWorldCoordY(y2), 1);

			firstTouchIsAttachedToStartPoint = setFirstTouchToStartPoint(
			        touch1,
			        touch2);

			if (firstTouchIsAttachedToStartPoint) {
				firstFingerTouch = touch1;
				secondFingerTouch = touch2;
			} else {
				firstFingerTouch = touch2;
				secondFingerTouch = touch1;
			}
			ec.twoTouchStartCommon(x1, y1, x2, y2);
		} else {
			ec.clearSelections();
			this.multitouchMode = MultitouchMode.view;
			ec.twoTouchStartCommon(x1, y1, x2, y2);
		}
	}

	/**
	 * @param geoElement
	 *            {@link GeoElement}
	 * @return true if GeoElement should be movable with two fingers
	 */
	private boolean isMovableWithTwoFingers(GeoElement geoElement) {
		return geoElement.getParentAlgorithm().getRelatedModeID() == EuclidianConstants.MODE_JOIN
		        || geoElement.getParentAlgorithm().getRelatedModeID() == EuclidianConstants.MODE_SEGMENT
		        || geoElement.getParentAlgorithm().getRelatedModeID() == EuclidianConstants.MODE_RAY;
	}

	/**
	 * @param touch1
	 *            {@link GeoPoint}
	 * @param touch2
	 *            {@link GeoPoint}
	 * @return true if the first touch should be attached to the startPoint
	 */
	private boolean setFirstTouchToStartPoint(GeoPoint touch1, GeoPoint touch2) {
		if (lineToMove.getStartPoint().getX() < lineToMove.getEndPoint().getX()) {
			return touch1.getX() < touch2.getX();
		}
		return touch2.getX() < touch1.getX();
	}

	/**
	 * screen coordinates
	 * 
	 * @param oldStartX
	 * @param oldStartY
	 * @param oldEndX
	 * @param oldEndY
	 * @param newStartX
	 * @param newStartY
	 * @param newEndX
	 * @param newEndY
	 * @return true if there are only minimal changes of the two finger-touches
	 */
	private boolean onlyJitter(double oldStartX, double oldStartY,
	        double oldEndX, double oldEndY, double newStartX, double newStartY,
	        double newEndX, double newEndY) {
		double capThreshold = app.getCapturingThreshold(PointerEventType.TOUCH);
		return Math.abs(oldStartX - newStartX) < capThreshold
		        && Math.abs(oldStartY - newStartY) < capThreshold
		        && Math.abs(oldEndX - newEndX) < capThreshold
		        && Math.abs(oldEndY - newEndY) < capThreshold;
	}

	public PointerEventType getDefaultEventType() {
		return ec.getDefaultEventType();
	}
}