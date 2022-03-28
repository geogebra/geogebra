package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.controller.MouseTouchGestureController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MyMath;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.DOM;

import elemental2.dom.WheelEvent;

/**
 * 3D euclidian controller
 *
 */
public class EuclidianController3DW extends EuclidianController3D implements
		IsEuclidianController {

	private MouseTouchGestureControllerW mtg;
	/**
	 * x-coordinates of the center of the multitouch-event
	 */
	protected int oldCenterX3D;
	/**
	 * y-coordinates of the center of the multitouch-event
	 */
	protected int oldCenterY3D;

	/**
	 * recalculates cached styles concerning browser environment
	 */
	@Override
	public void calculateEnvironment() {
		mtg.calculateEnvironment();
	}

	@Override
	protected void createCompanions() {
		super.createCompanions();
		mtg = new MouseTouchGestureControllerW((AppW) app, this);
	}

	/**
	 * Creates new controller
	 *
	 * @param kernel
	 *            kernel
	 */
	public EuclidianController3DW(Kernel kernel) {
		super(kernel.getApplication());
		setKernel(kernel);
	}

	@Override
	public void handleLongTouch(int x, int y) {
		if (!draggingOccured) {
			mtg.handleLongTouch(x, y);
		}
	}

	@Override
	public void setView(EuclidianView view) {
		super.setView(view);
		setView3D(view);
	}

	@Override
	public void onPointerEventStart(AbstractEvent event) {
		if (app.getGuiManager() != null) {
			((GuiManagerW) app.getGuiManager())
					.setActivePanelAndToolbar(App.VIEW_EUCLIDIAN3D);
		} else {
			setMode(EuclidianConstants.MODE_MOVE, ModeSetter.DOCK_PANEL);
		}
		mtg.onPointerEventStart(event);
	}

	public void onMouseWheel(WheelEvent event) {
		mtg.onMouseWheel(event);
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
	public void twoTouchStart(double x1, double y1, double x2, double y2) {
		oldCenterX3D = (int) (x1 + x2) / 2;
		oldCenterY3D = (int) (y1 + y2) / 2;

		twoTouchStartCommon(x1, y1, x2, y2);
	}

	@Override
	public void twoTouchMove(double x1, double y1, double x2, double y2) {

		int centerX = (int) ((x1 + x2) / 2);
		int centerY = (int) ((y1 + y2) / 2);

		// check zoom difference
		double newZoomDistance = 0;
		double zoomDiff = 0;
		if (this.getOldDistance() > 0) {
			newZoomDistance = MyMath.length(x1 - x2, y1 - y2);

			zoomDiff = Math.abs(newZoomDistance - this.getOldDistance());
			if (zoomDiff < MINIMAL_PIXEL_DIFFERENCE_FOR_ZOOM) {
				zoomDiff = 0;
			}
		}

		// check center difference
		double centerDiff = MyMath.length(oldCenterX3D - centerX, oldCenterY3D
		        - centerY);
		if (centerDiff <= MouseTouchGestureController.MIN_MOVE) {
			centerDiff = 0;
		}

		// process highest difference
		if (2 * centerDiff > zoomDiff) {
			getView().rememberOrigins();
			getView().setCoordSystemFromMouseMove(centerX - oldCenterX3D,
					centerY
					- oldCenterY3D, EuclidianController.MOVE_ROTATE_VIEW);
			viewRotationOccured = true;
			getView().repaintView();

			// update values
			oldCenterX3D = centerX;
			oldCenterY3D = centerY;
			this.setOldDistance(newZoomDistance);
		} else if (zoomDiff > 0) {
			onPinch(centerX, centerY, newZoomDistance / this.getOldDistance());

			// update values
			oldCenterX3D = centerX;
			oldCenterY3D = centerY;
			this.setOldDistance(newZoomDistance);
		}
	}

	/*
	 * specific methods for 3D controller
	 */

	@Override
	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		// no selection rectangle
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

			if (event instanceof PointerEvent) {
				DOM.setCapture(((PointerEvent) event).getRelativeElement());
			}

			super.wrapMouseDragged(event, startCapture);
		}
	}

	@Override
	public LongTouchManager getLongTouchManager() {
		return mtg.getLongTouchManager();
	}

	@Override
	public void setActualSticky(boolean b) {
		// TODO Auto-generated method stub
	}

	@Override
	public void closePopups(int x, int y, PointerEventType type) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPointerEventMove(PointerEvent event) {
		mtg.onMouseMoveNow(event, System.currentTimeMillis(), true);
	}

	@Override
	public void onPointerEventEnd(PointerEvent event) {
		mtg.onPointerEventEnd(event);
	}

	@Override
	public MouseTouchGestureControllerW getOffsets() {
		return mtg;
	}

	@Override
	public void hideDynamicStylebar() {
		if (getView().hasDynamicStyleBar()) {
			getView().getDynamicStyleBar().setVisible(false);
		}
	}

	@Override
	public void showDynamicStylebar() {
		if (app.isUnbundled() && ((AppW) app).allowStylebar()) {
			getView().getDynamicStyleBar().setVisible(true);
			getView().getDynamicStyleBar().updateStyleBar();
		}
	}
}
