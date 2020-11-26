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
import org.geogebra.web.html5.euclidian.profiler.FpsProfilerW;
import org.geogebra.web.html5.euclidian.profiler.drawer.DrawingEmulator;
import org.geogebra.web.html5.euclidian.profiler.drawer.DrawingRecorder;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

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
import com.google.gwt.user.client.Window;

@SuppressWarnings("javadoc")
public class MouseTouchGestureControllerW extends MouseTouchGestureController
		implements HasOffsets {

	private long lastMoveEvent = 0;
	private PointerEvent waitingTouchMove = null;
	private PointerEvent waitingMouseMove = null;

	private EnvironmentStyleW style = new EnvironmentStyleW();
	private boolean cssZoom = false;
	/**
	 * Threshold for the selection rectangle distance squared (10 pixel circle)
	 */
	public final static double SELECTION_RECT_THRESHOLD_SQR = 200.0;
	/**
	 * Threshold for the freehand tool distance squared (10 pixel circle)
	 */
	public final static double FREEHAND_MODE_THRESHOLD_SQR = 200.0;
	private int delayUntilMoveFinish = 150;

	/**
	 * flag for blocking the scaling of the axes
	 */
	protected boolean moveAxesAllowed = true;

	private LongTouchManager longTouchManager;

	private boolean dragModeMustBeSelected = false;
	private int deltaSum = 0;
	private int moveCounter = 0;
	private boolean dragModeIsRightClick = false;
	private LinkedList<PointerEvent> mousePool = new LinkedList<>();
	private LinkedList<PointerEvent> touchPool = new LinkedList<>();
	private boolean comboboxFocused;
	private boolean euclidianOffsetsInited = false;

	private DrawingEmulator drawingEmulator;
	private DrawingRecorder drawingRecorder;
	private boolean isRecording;

	/**
	 * ignore events after first touchEnd of a multi touch event
	 */
	private boolean ignoreEvent = false;

	public EnvironmentStyleW getEnvironmentStyle() {
		return style;
	}

	/**
	 * recalculates cached styles concerning browser environment
	 */
	public void calculateEnvironment() {
		if (ec.getView() == null) {
			return;
		}

		style = new EnvironmentStyleW();
		style.setxOffset(getEnvXoffset());
		style.setyOffset(getEnvYoffset());
		double scaleX = ((AppW) app).getGeoGebraElement().getScaleX();
		style.setScaleX(scaleX);
		style.setScaleY(((AppW) app).getGeoGebraElement().getScaleY());

		setZoomOffsets(scaleX);

		style.setScrollLeft(Window.getScrollLeft());
		style.setScrollTop(Window.getScrollTop());
		ec.getView().setPixelRatio(((AppW) app).getPixelRatio());
	}

	private void setZoomOffsets(double scale) {
		if (!cssZoom || scale == 1) {
			style.setZoomXOffset(0);
			style.setZoomYOffset(0);
		} else {
			style.setZoomXOffset(
					ec.getView().getAbsoluteLeft());
			style.setZoomYOffset(ec.getView().getAbsoluteTop());
		}
	}

	private int getEnvXoffset() {
		// return EuclidianViewXOffset;
		// the former solution doesn't update on scrolling
		return (((EuclidianViewWInterface) ec.getView()).getAbsoluteLeft()
				- Window.getScrollLeft());

	}

	/**
	 * @return offset to get correct getY() in mouseEvents
	 */
	private int getEnvYoffset() {
		// return EuclidianViewYOffset;
		// the former solution doesn't update on scrolling
		return ((EuclidianViewWInterface) ec.getView()).getAbsoluteTop()
		        - Window.getScrollTop();
	}

	public boolean isOffsetsUpToDate() {
		return euclidianOffsetsInited;
	}

	private Timer repaintTimer = new Timer() {
		@Override
		public void run() {
			moveIfWaiting();
		}
	};

	/**
	 * Handle waiting mouse/touch move event.
	 */
	public void moveIfWaiting() {
		long time = System.currentTimeMillis();
		if (this.waitingMouseMove != null) {
			this.onMouseMoveNow(waitingMouseMove, time, false);
			return;
		}
		if (this.waitingTouchMove != null) {
			this.onTouchMoveNow(waitingTouchMove, time, false);
		}
	}

	/**
	 * Create new mouse / touch handler.
	 *
	 * @param app
	 *            application
	 * @param ec
	 *            euclidian controller
	 */
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
		this.cssZoom = Browser.isSafariByVendor();
	}

	/**
	 * Fire touch event at the specified coordinates (right click if not unbundled)
	 */
	public void handleLongTouch(int x, int y) {
		PointerEvent event = new PointerEvent(x, y, PointerEventType.TOUCH,
		        ZeroOffset.INSTANCE);
		if (!app.isUnbundled()) {
			event.setIsRightClick(true);
		}
		ec.wrapMouseReleased(event);
	}

	/**
	 * @param event
	 *            gesture
	 */
	public void onGestureChange(GestureChangeEvent event) {
		// not handled
	}

	/**
	 * @param event
	 *            gesture
	 */
	public void onGestureEnd(GestureEndEvent event) {
		// not handled
	}

	/**
	 * @param event
	 *            gesture
	 */
	public void onGestureStart(GestureStartEvent event) {
		// not handled
	}

	/**
	 * @param event
	 *            touch cancel event
	 */
	public void onTouchCancel(TouchCancelEvent event) {
		// AbstractEvent e =
		// geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		Log.debug(event.getAssociatedType().getName());
	}

	/**
	 * @param event
	 *            touch move event
	 */
	public void onTouchMove(TouchMoveEvent event) {
		long time = System.currentTimeMillis();

		boolean killEvent = true;
		JsArray<Touch> targets = event.getTargetTouches();
		if (targets.length() == 1 && !ignoreEvent) {
			PointerEvent e0 = PointerEvent.wrapEvent(targets.get(0), this,
					event.getRelativeElement());
			if (isWholePageDrag()) {
				longTouchManager.rescheduleTimerIfRunning((LongTouchHandler) ec,
						e0.getX(), e0.getY(), true);
				killEvent = false;
			}

			if (time < this.lastMoveEvent
			        + EuclidianViewW.DELAY_BETWEEN_MOVE_EVENTS) {
				boolean wasWaiting = waitingTouchMove != null
				        || waitingMouseMove != null;
				this.waitingTouchMove = e0;
				this.waitingMouseMove = null;
				if (wasWaiting) {
					this.repaintTimer
							.schedule(delayUntilMoveFinish);
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

		if (killEvent) {
			event.stopPropagation();
			event.preventDefault();
		}

		CancelEventTimer.touchEventOccured();
	}

	/**
	 * Handle two finger touch move.
	 *
	 * @param touch
	 *            first touch
	 * @param touch2
	 *            second touch
	 */
	public void twoTouchMove(Touch touch, Touch touch2) {
		AbstractEvent first = PointerEvent.wrapEvent(touch, this);
		AbstractEvent second = PointerEvent.wrapEvent(touch2, this);
		ec.twoTouchMove(first.getX(), first.getY(), second.getX(),
		        second.getY());
		first.release();
		second.release();
	}

	/**
	 * Handle touch move event immediately.
	 *
	 * @param event
	 *            touch move
	 * @param time
	 *            current time
	 * @param startCapture
	 *            whether to start capturing
	 */
	public void onTouchMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		this.lastMoveEvent = time;
		// in SMART we actually get move events even if mouse button is up ...
		if (!dragModeMustBeSelected) {
			ec.wrapMouseMoved(event);
		} else {
			wrapMouseDraggedWithProfiling(event, startCapture);
		}

		this.waitingTouchMove = null;
		this.waitingMouseMove = null;
		int dragTime = (int) (System.currentTimeMillis() - time);
		if (dragTime > delayUntilMoveFinish) {
			delayUntilMoveFinish = dragTime + 10;
		}

		moveCounter++;
	}

	/**
	 * Handle touch end
	 *
	 * @param event
	 *            touch end event
	 */
	public void onTouchEnd(TouchEndEvent event) {
		Event.releaseCapture(event.getRelativeElement());
		dragModeMustBeSelected = false;
		if (moveCounter < 2) {
			ec.resetModeAfterFreehand();
		}

		this.moveIfWaiting();
		resetDelay();
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
					PointerEventType.TOUCH, ZeroOffset.INSTANCE));
		} else {
			// multitouch-event
			// ignore next touchMove and touchEnd events with one touch
			ignoreEvent = true;
		}
		CancelEventTimer.touchEventOccured();

		ec.resetModeAfterFreehand();
	}

	/**
	 * Ends the touch without the TouchEndEvent object.
	 */
	public void onTouchEnd() {
		dragModeMustBeSelected = false;
		if (moveCounter < 2) {
			ec.resetModeAfterFreehand();
		}

		this.moveIfWaiting();
		resetDelay();
		longTouchManager.cancelTimer();
		ec.wrapMouseReleased(new PointerEvent(ec.mouseLoc.x,
				ec.mouseLoc.y,
				PointerEventType.TOUCH, ZeroOffset.INSTANCE));
		CancelEventTimer.touchEventOccured();
		ec.resetModeAfterFreehand();
	}

	/**
	 * Handle touch start
	 *
	 * @param event
	 *            touch start event
	 */
	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		calculateEnvironment();
		CancelEventTimer.touchEventOccured();
		moveCounter = 0;
		ignoreEvent = false;
		ec.resetPinchZoomOccured();
		final boolean inputBoxFocused = false;
		ec.setDefaultEventType(PointerEventType.TOUCH, true);

		if (targets.length() == 1) {
			AbstractEvent e = PointerEvent.wrapEvent(targets.get(0), this);
			onPointerEventStart(e);
			if (isWholePageDrag()) {
				return;
			}
		} else if (targets.length() == 2) {
			longTouchManager.cancelTimer();
			twoTouchStart(targets.get(0), targets.get(1));
		} else {
			longTouchManager.cancelTimer();
		}
		if (!inputBoxFocused && !isWholePageDrag()) {
			preventTouchIfNeeded(event);
		}
	}

	private boolean isWholePageDrag() {
		boolean result = ec.getMode() == EuclidianConstants.MODE_MOVE
				&& !app.isShiftDragZoomEnabled()
				&& ec.getView().getHits().isEmpty();
		return result;
	}

	/**
	 * Prevent touch event default behavior unless needed for native elements
	 * (i.e. inputbox)
	 *
	 * @param event
	 *            touch event
	 */
	public void preventTouchIfNeeded(TouchStartEvent event) {
		if ((!ec.isTextfieldHasFocus()) && (!comboBoxHit())) {
			event.preventDefault();
		}
	}

	/**
	 * Handle double touch event.
	 *
	 * @param touch
	 *            first touch
	 * @param touch2
	 *            second touch
	 */
	public void twoTouchStart(Touch touch, Touch touch2) {
		calculateEnvironment();
		AbstractEvent first = PointerEvent.wrapEvent(touch, this);
		AbstractEvent second = PointerEvent.wrapEvent(touch2, this);
		ec.twoTouchStart(first.getX(), first.getY(), second.getX(),
		        second.getY());
		first.release();
		second.release();
		ec.getView().invalidateCache();
	}

	/**
	 * Handle mouse scroll.
	 *
	 * @param event
	 *            mouse wheel event
	 */
	public void onMouseWheel(MouseWheelEvent event) {
		// don't want to roll the scrollbar
		double delta = event.getDeltaY();
		// we are on device where many small scrolls come, we want to merge them
		int x = mouseEventX(event.getClientX() - style.getxOffset());
		int y = mouseEventX(event.getClientY() - style.getyOffset());
		boolean shiftOrMeta = event.isShiftKeyDown() || event.isMetaKeyDown();
		if (delta == 0) {
			deltaSum += getNativeDelta(event.getNativeEvent());
			if (Math.abs(deltaSum) > 40) {
				double ds = deltaSum;
				deltaSum = 0;
				ec.wrapMouseWheelMoved(x, y, ds,
						shiftOrMeta,
				        event.isAltKeyDown());
			}
			// normal scrolling
		} else {
			deltaSum = 0;
			ec.wrapMouseWheelMoved(x, y, delta,
					shiftOrMeta,
			        event.isAltKeyDown());
		}
		if (ec.allowMouseWheel(shiftOrMeta)) {
			event.preventDefault();
		}
	}

	private native double getNativeDelta(NativeEvent evt) /*-{
		return -evt.wheelDelta;
	}-*/;

	/**
	 * @param event
	 *            mouseover event
	 */
	public void onMouseOver(MouseOverEvent event) {
		ec.wrapMouseEntered();
	}

	/**
	 * Handle mouse out event.
	 *
	 * @param event
	 *            mouse out event.
	 */
	public void onMouseOut(MouseOutEvent event) {
		// cancel repaint to avoid closing newly opened tooltips
		repaintTimer.cancel();
		// hide dialogs if they are open
		((EuclidianViewWInterface) ec.getView()).resetPointerEventHandler();
		AbstractEvent e = PointerEvent.wrapEvent(event, this);
		ec.wrapMouseExited(e);
		e.release();
	}

	/**
	 * Handle mouse movement
	 *
	 * @param event
	 *            mouse move event
	 */
	public void onMouseMove(MouseMoveEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}

		if (ec.isExternalHandling()) {
			return;
		}

		PointerEvent e = PointerEvent.wrapEvent(event, this);
		event.preventDefault();
		long time = System.currentTimeMillis();

		if (time < this.lastMoveEvent
		        + EuclidianViewW.DELAY_BETWEEN_MOVE_EVENTS) {

			boolean wasWaiting = waitingTouchMove != null
			        || waitingMouseMove != null;
			this.waitingMouseMove = e;
			this.setWaitingTouchMove(null);
			if (wasWaiting) {
				this.repaintTimer
						.schedule(delayUntilMoveFinish);
			}
			if (ec.getView().getMode() != EuclidianConstants.MODE_FREEHAND_SHAPE
					&& ec.getView().getMode() != EuclidianConstants.MODE_PEN) {
				return;
			}
		}

		onMouseMoveNow(e, time, true);
	}

	private void setWaitingTouchMove(PointerEvent o) {
		waitingTouchMove = o;
	}

	/**
	 * Handle mouse move event immediately.
	 *
	 * @param event
	 *            touch move
	 * @param time
	 *            current time
	 * @param startCapture
	 *            whether to start capturing
	 */
	public void onMouseMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		this.lastMoveEvent = time;
		if (!dragModeMustBeSelected) {
			ec.wrapMouseMoved(event);
		} else {
			event.setIsRightClick(dragModeIsRightClick);
			wrapMouseDraggedWithProfiling(event, startCapture);
			if (isRecording) {
				drawingRecorder
						.recordCoordinate(event.getX(), event.getY(), System.currentTimeMillis());
			}
		}
		event.release();
		this.waitingMouseMove = null;
		this.waitingTouchMove = null;
		int dragTime = (int) (System.currentTimeMillis() - time);
		if (dragTime > delayUntilMoveFinish) {
			delayUntilMoveFinish = dragTime + 10;
		}

		moveCounter++;
	}

	private void wrapMouseDraggedWithProfiling(PointerEvent event, boolean startCapture) {
		double dragStart = FpsProfilerW.getMillisecondTimeNative();
		ec.wrapMouseDragged(event, startCapture);
		GeoGebraProfiler.addDrag(
				(long) (FpsProfilerW.getMillisecondTimeNative() - dragStart));
	}

	/**
	 * Handle mouse up event.
	 *
	 * @param event
	 *            mouse up event
	 */
	public void onMouseUp(MouseUpEvent event) {
		Event.releaseCapture(event.getRelativeElement());
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}

		event.preventDefault();
		AbstractEvent e = PointerEvent.wrapEvent(event, this);
		onPointerEventEnd(e);
	}

	/**
	 * Handle touch end or mouse up.
	 *
	 * @param e
	 *            pointer up event
	 */
	public void onPointerEventEnd(AbstractEvent e) {
		app.getFpsProfiler().notifyTouchEnd();
		if (isRecording) {
			drawingRecorder.recordTouchEnd();
		}
		if (moveCounter < 2) {
			ec.resetModeAfterFreehand();
		}
		this.moveIfWaiting();
		resetDelay();
		dragModeMustBeSelected = false;

		// hide dialogs if they are open
		// but don't hide context menu if we just opened it via long tap in IE
		if (ec.getDefaultEventType() == PointerEventType.MOUSE
				&& app.getGuiManager() != null) {
			((AppW) app).getGuiManager().removePopup();
		}

		ec.wrapMouseReleased(e);
		e.release();

		ec.resetModeAfterFreehand();
	}

	/**
	 * Handle mouse down event.
	 *
	 * @param event
	 *            mouse down event
	 */
	public void onMouseDown(MouseDownEvent event) {
		deltaSum = 0;

		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		// No prevent default here: make sure keyboard focus goes to canvas
		AbstractEvent e = PointerEvent.wrapEvent(event, this);
		ec.setDefaultEventType(PointerEventType.MOUSE, true);
		ec.onPointerEventStart(e);

		moveCounter = 0;
		ignoreEvent = false;
	}

	/**
	 * Handle pointer start event.
	 *
	 * @param event
	 *            pointer start
	 */
	public void onPointerEventStart(AbstractEvent event) {
		app.getFpsProfiler().notifyTouchStart();

		if (((AppW) app).isMenuShowing()) {
			((AppW) app).toggleMenu();
		}
		if (isRecording) {
			drawingRecorder
					.recordCoordinate(event.getX(), event.getY(), System.currentTimeMillis());
		}
		if (event.getType() == PointerEventType.TOUCH
				&& EuclidianConstants.isMoveOrSelectionMode(ec.getMode())) {
			longTouchManager.scheduleTimer((LongTouchHandler) ec, event.getX(), event.getY());
		}

		if (!ec.isTextfieldHasFocus()) {
			dragModeMustBeSelected = true;
			dragModeIsRightClick = event.isRightClick();
		}

		ec.wrapMousePressed(event);
		// hide PopUp if no hits was found.
		if (ec.getView().getHits().isEmpty() && ec.getView().hasStyleBar()) {
			ec.getView().getStyleBar().hidePopups();
		}
		if (!event.isRightClick()) {
			ec.prepareModeForFreehand();
		}
		event.release();
	}

	private boolean comboBoxHit() {
		if (ec.getView().getHits() == null) {
			return false;
		}
		int i = 0;
		while (i < ec.getView().getHits().size()) {
			GeoElement hit = ec.getView().getHits().get(i++);
			if (hit instanceof GeoList && ((GeoList) hit).drawAsComboBox()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initialize tooltip manager.
	 */
	public void initToolTipManager() {
		ToolTipManagerW.sharedInstance();
	}

	public void resetToolTipManager() {
		// TODO Auto-generated method stub
	}

	/**
	 * @return whether reset icon was hit
	 */
	public boolean hitResetIcon() {
		return app.showResetIcon()
				&& ((ec.mouseLoc.y < 32) && (ec.mouseLoc.x > (ec.getView().getViewWidth() - 32)));
	}

	@Override
	public LinkedList<PointerEvent> getMouseEventPool() {
		return mousePool;
	}

	@Override
	public LinkedList<PointerEvent> getTouchEventPool() {
		return touchPool;
	}

	public boolean isComboboxFocused() {
		return this.comboboxFocused;
	}

	public void setComboboxFocused(boolean flag) {
		this.comboboxFocused = flag;
	}

	@Override
	public int touchEventX(int clientX) {
		return mouseEventX(clientX - style.getxOffset());
	}

	@Override
	public int touchEventY(int clientY) {
		return mouseEventY(clientY - style.getyOffset());
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public double getScaleXMultiplier() {
		return style.getScaleXMultiplier();
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public double getScaleYMultiplier() {
		return style.getScaleYMultiplier();
	}

	@Override
	public int mouseEventX(int clientX) {
		return getEventCoordInGraphics(clientX, style.getZoomXOffset(),
				style.getScaleX());
	}

	private static int getEventCoordInGraphics(int relativePosition,
			double zoomOffset, double scale) {
		double absPosition = relativePosition + zoomOffset;
		return (int) Math.round(absPosition / scale - zoomOffset);
	}

	@Override
	public int mouseEventY(int clientY) {
		return getEventCoordInGraphics(clientY, style.getZoomYOffset(),
				style.getScaleY());
	}

	@Override
	public int getEvID() {
		return ec.getView().getViewID();
	}

	@Override
	public PointerEventType getDefaultEventType() {
		return ec.getDefaultEventType();
	}

	public LongTouchManager getLongTouchManager() {
		return longTouchManager;
	}

	public void resetDelay() {
		delayUntilMoveFinish = 150;
	}

	/**
	 * Close all popups.
	 */
	public void closePopups(PointerEvent e) {
		((AppW) app).onUnhandledClick();
		app.closePopups(e.getX(), e.getY());
	}

	/**
	 * @return drawing emulator
	 */
	public DrawingEmulator getDrawingEmulator() {
		if (drawingEmulator == null) {
			drawingEmulator = new DrawingEmulator(this);
		}
		return drawingEmulator;
	}

	/**
	 * Records the drawing.
	 */
	public void startDrawRecording() {
		isRecording = true;
		if (drawingRecorder == null) {
			drawingRecorder = new DrawingRecorder();
		}
	}

	/**
	 * Ends the recording of the drawing and logs the results.
	 *
	 * For autonomous drawing, the logged result has to be copied into the coords.json file.
	 */
	public void endDrawRecordingAndLogResult() {
		Log.debug(drawingRecorder);
		drawingRecorder.reset();
		isRecording = false;
	}
}
