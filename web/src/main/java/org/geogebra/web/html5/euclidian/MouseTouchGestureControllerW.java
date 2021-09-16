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

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Window;

import elemental2.dom.WheelEvent;
import jsinterop.base.Js;

@SuppressWarnings("javadoc")
public class MouseTouchGestureControllerW extends MouseTouchGestureController
		implements HasOffsets {

	private PointerEvent waitingTouchMove = null;
	private PointerEvent waitingMouseMove = null;

	private EnvironmentStyleW style = new EnvironmentStyleW();
	private boolean cssZoom = false;

	private int delayUntilMoveFinish = 150;

	private LongTouchManager longTouchManager;

	private boolean dragModeMustBeSelected = false;
	private int deltaSum = 0;
	private int moveCounter = 0;
	private boolean dragModeIsRightClick = false;
	private LinkedList<PointerEvent> mousePool = new LinkedList<>();
	private LinkedList<PointerEvent> touchPool = new LinkedList<>();
	private boolean comboboxFocused;

	private DrawingEmulator drawingEmulator;
	private DrawingRecorder drawingRecorder;
	private boolean isRecording;

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

		app.getGlobalHandlers().add(Window.addWindowScrollHandler(e -> calculateEnvironment()));
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
		int x = event.getClientX();
		int y = event.getClientY();
		boolean shiftOrMeta = event.isShiftKeyDown() || event.isMetaKeyDown();
		if (delta == 0) {
			deltaSum += getNativeDelta(event);
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

	public static double getNativeDelta(MouseWheelEvent evt) {
		return Js.<WheelEvent>uncheckedCast(evt.getNativeEvent()).deltaY;
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
