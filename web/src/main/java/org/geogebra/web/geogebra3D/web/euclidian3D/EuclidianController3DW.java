package org.geogebra.web.geogebra3D.web.euclidian3D;

import java.util.LinkedList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MyMath;
import org.geogebra.web.html5.euclidian.EnvironmentStyleW;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;

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
import com.google.gwt.user.client.Event;

public class EuclidianController3DW extends EuclidianController3D implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, MouseOutHandler,
        MouseOverHandler, MouseWheelHandler, TouchStartHandler,
        TouchEndHandler, TouchMoveHandler, TouchCancelHandler,
        GestureStartHandler, GestureEndHandler, GestureChangeHandler,
		HasOffsets, IsEuclidianController {

	private AbstractEvent waitingTouchMove = null;
	private PointerEvent waitingMouseMove = null;


	@Override
	public EnvironmentStyleW getEnvironmentStyle() {
		return mtg.getEnvironmentStyle();
	}


	/**
	 * recalculates cached styles concerning browser environment
	 */
	@Override
	public void calculateEnvironment() {
		mtg.calculateEnvironment();
	}

	@Override
	public void moveIfWaiting() {
		mtg.moveIfWaiting();

	}




	public boolean isOffsetsUpToDate() {
		return mtg.isOffsetsUpToDate();
	}


	private MouseTouchGestureControllerW mtg;

	@Override
	protected void createCompanions() {
		super.createCompanions();
		mtg = new MouseTouchGestureControllerW((AppW) app, this);
	}


	public EuclidianController3DW(Kernel kernel) {
		super(kernel.getApplication());
		setKernel(kernel);
		// RealSense.initIfSupported(this);
		// RealSense.createInstance();

		tempNum = new MyDouble(kernel);
	}

	public void handleLongTouch(int x, int y) {
		mtg.handleLongTouch(x, y);
	}

	@Override
	public void setView(EuclidianView view) {
		this.view = view;
		setView3D(view);
	}

	@Override
	public void onGestureChange(GestureChangeEvent event) {
		mtg.onGestureChange(event);
	}

	@Override
	public void onGestureEnd(GestureEndEvent event) {
		mtg.onGestureEnd(event);
	}

	@Override
	public void onGestureStart(GestureStartEvent event) {
		mtg.onGestureStart(event);
	}


	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		mtg.onTouchCancel(event);
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		mtg.onTouchMove(event);
	}

	public void twoTouchMove(Touch touch, Touch touch2) {
		mtg.twoTouchMove(touch, touch2);

	}


	private void onTouchMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		mtg.onTouchMoveNow(event, time, startCapture);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		mtg.onTouchEnd(event);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if (app.getGuiManager() != null) {
			((GuiManagerW) app.getGuiManager())
			        .setActiveToolbarId(App.VIEW_EUCLIDIAN3D);
		} else {
			setMode(EuclidianConstants.MODE_MOVE);
			// app.setMode(EuclidianConstants.MODE_MOVE);
			// app.getGuiManager().updateToolbar();
		}
		mtg.onTouchStart(event);

	}

	public void onPointerEventStart(AbstractEvent event) {
		if (app.getGuiManager() != null) {
			((GuiManagerW) app.getGuiManager())
			        .setActiveToolbarId(App.VIEW_EUCLIDIAN3D);
		} else {
			setMode(EuclidianConstants.MODE_MOVE);
			// app.setMode(EuclidianConstants.MODE_MOVE);
			// app.getGuiManager().updateToolbar();
		}
		mtg.onPointerEventStart(event);
	}

	public void preventTouchIfNeeded(TouchStartEvent event) {
		mtg.preventTouchIfNeeded(event);
	}

	public void twoTouchStart(Touch touch, Touch touch2) {
		mtg.twoTouchStart(touch, touch2);
	}


	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		mtg.onMouseWheel(event);
	}



	@Override
	public void onMouseOver(MouseOverEvent event) {
		mtg.onMouseOver(event);
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		mtg.onMouseOut(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		mtg.onMouseMove(event);
	}

	public void onMouseMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		mtg.onMouseMoveNow(event, time, startCapture);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mtg.onMouseUp(event);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		mtg.onMouseDown(event);
	}


	@Override
	protected void initToolTipManager() {
		mtg.initToolTipManager();

	}

	@Override
	protected void resetToolTipManager() {
		mtg.resetToolTipManager();

	}

	@Override
	protected boolean hitResetIcon() {
		return mtg.hitResetIcon();
	}

	@Override
	public LinkedList<PointerEvent> getMouseEventPool() {
		return mtg.getMouseEventPool();
	}


	@Override
	public LinkedList<PointerEvent> getTouchEventPool() {
		return mtg.getTouchEventPool();
	}


	@Override
	public boolean textfieldJustFocusedW(int x, int y, PointerEventType type) {
		return view.textfieldClicked(x, y, type);
	}

	public boolean isComboboxFocused() {
		return mtg.isComboboxFocused();
	}

	public void setComboboxFocused(boolean flag) {
		mtg.setComboboxFocused(flag);
	}

	@Override
	public int touchEventX(int clientX) {
		return mtg.touchEventX(clientX);
	}

	@Override
	public int touchEventY(int clientY) {
		return mtg.touchEventY(clientY);
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public float getScaleXMultiplier() {
		return mtg.getScaleXMultiplier();
	}

	/**
	 * @return the multiplier that must be used to multiply the native event
	 *         coordinates
	 */
	public float getScaleYMultiplier() {
		return mtg.getScaleYMultiplier();
	}

	@Override
	public int mouseEventX(int clientX) {
		return mtg.mouseEventX(clientX);
	}

	@Override
	public int mouseEventY(int clientY) {
		return mtg.mouseEventY(clientY);
	}


	/**
	 * coordinates of the center of the multitouch-event
	 */
	protected int oldCenterX, oldCenterY;

	@Override
	public void twoTouchStart(double x1, double y1, double x2, double y2) {

		oldCenterX = (int) (x1 + x2) / 2;
		oldCenterY = (int) (y1 + y2) / 2;

		twoTouchStartCommon(x1, y1, x2, y2);

	}

	@Override
	public void twoTouchMove(double x1, double y1, double x2, double y2) {

		int centerX = (int) ((x1 + x2) / 2);
		int centerY = (int) ((y1 + y2) / 2);

		// check zoom difference
		double newZoomDistance = 0;
		double zoomDiff = 0;
		if (this.oldDistance > 0) {
			newZoomDistance = MyMath.length(x1 - x2, y1 - y2);

			zoomDiff = Math.abs(newZoomDistance - this.oldDistance);
			if (zoomDiff < MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM) {
				zoomDiff = 0;
			}
		}

		// check center difference
		double centerDiff = MyMath.length(oldCenterX - centerX, oldCenterY
		        - centerY);
		if (centerDiff <= MouseTouchGestureControllerW.MIN_MOVE) {
			centerDiff = 0;
		}

		// process highest difference
		if (2 * centerDiff > zoomDiff) {
			view.rememberOrigins();
			view.setCoordSystemFromMouseMove(centerX - oldCenterX, centerY
			        - oldCenterY, EuclidianController.MOVE_ROTATE_VIEW);
			viewRotationOccured = true;
			view.repaintView();

			// update values
			oldCenterX = centerX;
			oldCenterY = centerY;
			this.oldDistance = newZoomDistance;
		} else if (zoomDiff > 0) {
			onPinch(centerX, centerY, newZoomDistance / this.oldDistance);

			// update values
			oldCenterX = centerX;
			oldCenterY = centerY;
			this.oldDistance = newZoomDistance;
		}


	}

	// /////////////////////////////////////////////////////
	// specific methods for 3D controller
	// /////////////////////////////////////////////////////

	/**
	 * @param mx
	 * @param my
	 * @param mz
	 * @param ox
	 * @param oy
	 * @param oz
	 * @param ow
	 * @param name
	 */
	public void onHandValues(int mx, int my, int mz, int ox, int oy, int oz,
	        int ow, String name) {

		App.debug(mx + "," + my + "," + mz + " -- " + name);

	}

	@Override
	public int getEvID() {
		return view.getEuclidianViewNo();
	}

	@Override
	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		// TODO

	}

	@Override
	protected void processMouseMoved(AbstractEvent e) {

		super.processMouseMoved(e);
		processMouseMoved();

	}

	@Override
	public void update() {
		// no picking with shaders
	}

	@Override
	public void wrapMouseDragged(AbstractEvent event, boolean startCapture) {

		if (!shouldCancelDrag()) {
			// Set capture events only if the mouse is actually down,
			// because we need to release the capture on mouse up.
			if (waitingMouseMove == null && waitingTouchMove == null) {
				Event.setCapture(((PointerEvent) event).getRelativeElement());
			}
			super.wrapMouseDragged(event, startCapture);
		}

	}

	public LongTouchManager getLongTouchManager() {
		return mtg.getLongTouchManager();
	}

	public void setActualSticky(boolean b) {
		// TODO Auto-generated method stub

	}

}
