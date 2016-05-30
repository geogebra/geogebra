package org.geogebra.web.html5.euclidian;

import java.util.LinkedList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.controller.MouseTouchGestureController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.main.AppW;

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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class MouseTouchGestureControllerW extends MouseTouchGestureController
		implements HasOffsets {

	private AppW app;

	private long lastMoveEvent = 0;
	private PointerEvent waitingTouchMove = null;
	private PointerEvent waitingMouseMove = null;

	public EnvironmentStyleW style = new EnvironmentStyleW();

	/**
	 * Threshold for the selection rectangle distance squared (10 pixel circle)
	 */
	public final static double SELECTION_RECT_THRESHOLD_SQR = 200.0;
	public final static double FREEHAND_MODE_THRESHOLD_SQR = 200.0;

	/**
	 * flag for blocking the scaling of the axes
	 */
	protected boolean moveAxesAllowed = true;

	private int previousMode = -1;

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
		style.setxOffset(getEnvXoffset());
		style.setyOffset(getEnvYoffset());
		style.setScaleX(app.getArticleElement().getScaleX());
		style.setScaleY(app.getArticleElement().getScaleY());
		style.setScrollLeft(Window.getScrollLeft());
		style.setScrollTop(Window.getScrollTop());
		ec.view.setPixelRatio(app.getPixelRatio());

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
		super(app, ec);
		this.app = app;

		Window.addWindowScrollHandler(new Window.ScrollHandler() {

			@Override
			public void onWindowScroll(Window.ScrollEvent event) {
				calculateEnvironment();
			}
		});
		app.addWindowResizeListener(this);
		longTouchManager = LongTouchManager.getInstance();
	}

	public void handleLongTouch(int x, int y) {
		Log.debug("LONG TOUCH");
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
				if (ec.isDraggingBeyondThreshold(1)) {
				longTouchManager.rescheduleTimerIfRunning(
				        (LongTouchHandler) ec, e.getX(),
				        e.getY(), false);
				}
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
		boolean inputBoxFocused = false;
		if (targets.length() == 1) {
			AbstractEvent e = PointerEvent.wrapEvent(targets.get(0), this);
			if (ec.getMode() == EuclidianConstants.MODE_MOVE) {
				longTouchManager.scheduleTimer((LongTouchHandler) ec, e.getX(),
				        e.getY());
			}
			// inputBoxFocused = ec.textfieldJustFocusedW(e.getX(), e.getY(),
			// e.getType());
			onPointerEventStart(e);
		} else if (targets.length() == 2) {
			longTouchManager.cancelTimer();
			twoTouchStart(targets.get(0), targets.get(1));
		} else {
			longTouchManager.cancelTimer();
		}
		if (!inputBoxFocused) {
			preventTouchIfNeeded(event);
		}
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
		// but don't hide context menu if we just opened it via long tap in IE
		if (ec.getDefaultEventType() == PointerEventType.MOUSE
				&& app.getGuiManager() != null)
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
		// No prevent default here: make sure keyboard focus goes to canvas
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
		ToolTipManagerW.sharedInstance();
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
		return Math.round((clientX) * (1 / style.getScaleX()));
	}

	@Override
	public int mouseEventY(int clientY) {
		return Math.round((clientY) * (1 / style.getScaleY()));
	}

	@Override
	public int getEvID() {
		return ec.view.getViewID();
	}

	public PointerEventType getDefaultEventType() {
		return ec.getDefaultEventType();
	}

	public LongTouchManager getLongTouchManager() {
		return longTouchManager;
	}
}