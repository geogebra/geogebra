/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.desktop.geogebra3D.euclidian3D;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.ToolTipManager;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.desktop.euclidian.EuclidianControllerListeners;
import org.geogebra.desktop.euclidian.event.MouseEventD;
import org.geogebra.desktop.main.AppD;

/**
 * EuclidianController.java
 * 
 * Created on 16. October 2001, 15:41
 */
public class EuclidianController3DD extends EuclidianController3D
		implements EuclidianControllerListeners {


	private int defaultInitialDelay;

	/***********************************************
	 * Creates new EuclidianController
	 * 
	 * @param kernel
	 *            Kernel
	 **********************************************/
	public EuclidianController3DD(Kernel kernel) {
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
	public void setView(EuclidianView view) {
		super.setView(view);
		setView3D(view);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// let mousePressed and mouseReleased take care of this
	}

	@Override
	public void mousePressed(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		wrapMousePressed(event);
		event.release();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		// no capture in desktop
		wrapMouseDragged(event, false);
		event.release();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		AbstractEvent event = MouseEventD.wrapEvent(e);
		wrapMouseReleased(event);
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

	@Override
	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		if (getView() != null) {
			getView().updateSize();
		}
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

	@Override
	public boolean refreshHighlighting(Hits hits, boolean control) {

		if (((AppD) app).getShiftDown()) {
			return false;
		}

		return super.refreshHighlighting(hits, control);
	}

	@Override
	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		// TODO

	}

	@Override
	public void closePopups(int x, int y, PointerEventType type) {
		// TODO Auto-generated method stub

	}

}
