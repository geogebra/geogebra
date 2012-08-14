/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.main.AppD;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ToolTipManager;

/**
 * EuclidianController.java
 * 
 * Created on 16. October 2001, 15:41
 */
public class EuclidianControllerD extends
		geogebra.common.euclidian.EuclidianController implements MouseListener,
		MouseMotionListener, MouseWheelListener, ComponentListener {

	// protected GeoVec2D b;

	// protected GeoSegment movedGeoSegment;

	// protected MyPopupMenu popupMenu;

	// boolean polygonRigid = false;

	/***********************************************
	 * Creates new EuclidianController
	 **********************************************/
	public EuclidianControllerD(Kernel kernel) {
		setKernel(kernel);
		setApplication(kernel.getApplication());

		// for tooltip manager
		DEFAULT_INITIAL_DELAY = ToolTipManager.sharedInstance()
				.getInitialDelay();

		tempNum = new MyDouble(kernel);
	}

	@Override
	public void setApplication(App app) {
		this.app = app;
	}

	@Override
	public AppD getApplication() {
		return (AppD) app;
	}

	protected void setView(EuclidianView view) {
		// void setView(EuclidianView view) {
		this.view = view;
	}

	public void mouseClicked(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseclicked(event);
		event.release();
	}

	public void mousePressed(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMousePressed(event);
		event.release();
	}

	public void mouseDragged(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseDragged(event);
		event.release();
	}

	public void mouseReleased(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseReleased(event);
		event.release();
	}

	public void mouseMoved(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseMoved(event);
		event.release();
	}

	public void mouseEntered(MouseEvent e) {
		wrapMouseEntered();
	}

	public void mouseExited(MouseEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
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
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY / 2);
		ttm.setEnabled(((AppD) app).getAllowToolTips());
	}

	@Override
	public void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
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
	public GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { kernel.getManager3D().Circle3D(null, p0, p1,
				view.getDirection()) };
	}

	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		((EuclidianViewND) view).updateSize();
	}

	public void componentShown(ComponentEvent e) {
		// do nothing
	}

	public void componentHidden(ComponentEvent e) {
		// do nothing
	}

	public void componentMoved(ComponentEvent e) {
		// do nothing
	}

	/**
	 * Zooms in or out using mouse wheel
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		AbstractEvent event = geogebra.euclidian.event.MouseEvent.wrapEvent(e);
		wrapMouseWheelMoved(event);
		event.release();
	}

}
