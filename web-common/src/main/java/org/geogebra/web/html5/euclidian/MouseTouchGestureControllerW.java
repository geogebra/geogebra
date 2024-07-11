package org.geogebra.web.html5.euclidian;

import java.util.LinkedList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.controller.MouseTouchGestureController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.profiler.FpsProfilerW;
import org.geogebra.web.html5.euclidian.profiler.drawer.DrawingEmulator;
import org.geogebra.web.html5.euclidian.profiler.drawer.DrawingRecorder;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.main.AppW;

import elemental2.dom.DomGlobal;
import elemental2.dom.WheelEvent;

public class MouseTouchGestureControllerW extends MouseTouchGestureController
		implements HasOffsets {

	private int delayUntilMoveFinish = 150;

	private final LongTouchManager longTouchManager;

	private boolean dragModeMustBeSelected = false;
	private int moveCounter = 0;
	private boolean dragModeIsRightClick = false;
	private final LinkedList<PointerEvent> mousePool = new LinkedList<>();
	private final LinkedList<PointerEvent> touchPool = new LinkedList<>();
	private boolean comboboxFocused;

	private DrawingEmulator drawingEmulator;
	private DrawingRecorder drawingRecorder;
	private boolean isRecording;

	/**
	 * recalculates cached styles concerning browser environment
	 */
	public void calculateEnvironment() {
		if (ec.getView() == null) {
			return;
		}

		ec.getView().setPixelRatio(((AppW) app).getPixelRatio());
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

		app.getGlobalHandlers().addEventListener(DomGlobal.window, "scroll",
				e -> calculateEnvironment());
		app.addWindowResizeListener(this::calculateEnvironment);
		longTouchManager = LongTouchManager.getInstance();
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
	 * Handle mouse scroll.
	 *
	 * @param event
	 *            mouse wheel event
	 */
	public void onMouseWheel(WheelEvent event) {
		// don't want to roll the scrollbar
		double delta = event.deltaY;
		// we are on device where many small scrolls come, we want to merge them
		int x = (int) Math.round(event.offsetX / getZoomLevel());
		int y = (int) Math.round(event.offsetY / getZoomLevel());
		boolean shiftOrMeta = event.shiftKey || event.metaKey;
		boolean consumed = false;
		if (delta != 0) {
			consumed = ec.wrapMouseWheelMoved(x, y, delta,
					shiftOrMeta, event.altKey);
		}
		if (consumed || ec.allowMouseWheel(shiftOrMeta)) {
			event.preventDefault();
		}
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
	public void onMouseMoveNow(PointerEvent event, long time, boolean startCapture) {
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

	public void resetToolTipManager() {
		// TODO Auto-generated method stub
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

	@Override
	public double getZoomLevel() {
		String zoom = ((AppW) app).getGeoGebraElement().getParentElement()
				.getStyle().getProperty("zoom");
		if (StringUtil.empty(zoom)) {
			return 1;
		}
		try {
			return Double.parseDouble(zoom);
		} catch (NumberFormatException e) {
			return 1;
		}
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
