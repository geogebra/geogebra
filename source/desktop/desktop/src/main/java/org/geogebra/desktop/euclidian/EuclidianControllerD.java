/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.desktop.euclidian;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.ToolTipManager;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.desktop.euclidian.event.MouseEventD;
import org.geogebra.desktop.main.AppD;

/**
 * EuclidianController.java
 * 
 * Created on 16. October 2001, 15:41
 */
public class EuclidianControllerD extends EuclidianController
		implements EuclidianControllerListeners {

	private int moveCounter = 0;
	private int defaultInitialDelay;

	/***********************************************
	 * Creates new EuclidianController
	 * 
	 * @param kernel
	 *            kernel
	 **********************************************/
	public EuclidianControllerD(Kernel kernel) {
		super(kernel.getApplication());
		setKernel(kernel);

		// for tooltip manager
		defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();

	}

	@Override
	public AppD getApplication() {
		return (AppD) app;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// let mousePressed and mouseReleased take care of this
	}

	@Override
	public void mousePressed(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		closePopups(event.getX(), event.getY(), null);
		wrapMousePressed(event);
		if (!app.isRightClick(event)) {
			prepareModeForFreehand();
		}
		moveCounter = 0;
		event.release();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		// no capture in desktop
		wrapMouseDragged(event, true);
		moveCounter++;
		event.release();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		if (moveCounter < 2) {
			this.resetModeAfterFreehand();
		}
		wrapMouseReleased(event);
		this.resetModeAfterFreehand();
		event.release();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		wrapMouseMoved(event);
		event.release();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		wrapMouseEntered();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		wrapMouseExited(event);
		event.release();
	}

	/*
	 * public void focusGained(FocusEvent e) { initToolTipManager(); }
	 * 
	 * public void focusLost(FocusEvent e) { resetToolTipManager(); }
	 */

	@Override
	public void initToolTipManager() {
		// set tooltip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(defaultInitialDelay / 2);
		ttm.setEnabled(((AppD) app).getAllowToolTips());
	}

	@Override
	public void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(defaultInitialDelay);
	}

	/* ****************************************************** */

	/*
	 * final protected void transformCoords(boolean usePointCapturing) { // calc
	 * real world coords calcRWcoords();
	 * 
	 * if (usePointCapturing) { double pointCapturingPercentage = 1; switch
	 * (view.getPointCapturingMode()) { case
	 * EuclidianConstants.POINT_CAPTURING_AUTOMATIC: if
	 * (!view.isGridOrAxesShown())break;
	 * 
	 * case EuclidianView.POINT_CAPTURING_ON: pointCapturingPercentage = 0.125;
	 * 
	 * case EuclidianView.POINT_CAPTURING_ON_GRID: // X = (x, y) ... next grid
	 * point double x = Kernel.roundToScale(xRW, view.gridDistances[0]); double
	 * y = Kernel.roundToScale(yRW, view.gridDistances[1]); // if |X - XRW| <
	 * gridInterval * pointCapturingPercentage then take the grid point double a
	 * = Math.abs(x - xRW); double b = Math.abs(y - yRW); if (a <
	 * view.gridDistances[0] * pointCapturingPercentage && b <
	 * view.gridDistances[1] * pointCapturingPercentage) { xRW = x; yRW = y;
	 * mouseLoc.x = view.toScreenCoordX(xRW); mouseLoc.y =
	 * view.toScreenCoordY(yRW); }
	 * 
	 * default: // point capturing off } } }
	 */

	// fetch the two selected points
	/*
	 * protected void join(){ GeoPoint[] points = getSelectedPoints(); GeoLine
	 * line = kernel.Line(null, points[0], points[1]); }
	 */

	@Override
	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		getView().updateSize();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// do nothing
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// do nothing
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// do nothing
	}

	/**
	 * Zooms in or out using mouse wheel
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		wrapMouseWheelMoved(e.getX(), e.getY(), e.getPreciseWheelRotation(),
				e.isShiftDown() || e.isMetaDown(), e.isAltDown());
	}

	@Override
	public void addListenersTo(Component evjpanel) {
		evjpanel.addMouseMotionListener(this);
		evjpanel.addMouseListener(this);
		evjpanel.addMouseWheelListener(this);
		evjpanel.addComponentListener(this);
	}

}
