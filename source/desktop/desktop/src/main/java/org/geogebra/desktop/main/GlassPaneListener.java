/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.main;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import org.geogebra.desktop.euclidian.event.MouseEventUtil;

/**
 * Dispatches all mouse and key events from the glass pane to a given component.
 */
public class GlassPaneListener
		implements MouseListener, MouseMotionListener, KeyEventDispatcher {

	private final Component glassPane;
	private final Component destComp;
	private final Container contentPane;

	/**
	 * @param glassPane
	 *            glass pane
	 * @param contentPane
	 *            container
	 * @param destComp
	 *            destination component
	 */
	public GlassPaneListener(Component glassPane, Container contentPane,
			Component destComp) {
		this.glassPane = glassPane;
		this.contentPane = contentPane;
		this.destComp = destComp;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		redispatchMouseEvent(e);
	}

	private void redispatchMouseEvent(MouseEvent e) {
		Point glassPanePoint = e.getPoint();
		Container container = contentPane;
		Point containerPoint = SwingUtilities.convertPoint(glassPane,
				glassPanePoint, contentPane);

		// Find out exactly which component the mouse event is over.
		Component component = SwingUtilities.getDeepestComponentAt(container,
				containerPoint.x, containerPoint.y);

		if ((component != null) && (component.equals(destComp))) {
			// Forward events to the destination comp
			Point componentPoint = SwingUtilities.convertPoint(glassPane,
					glassPanePoint, component);
			component.dispatchEvent(new MouseEvent(component, e.getID(),
					e.getWhen(), e.getModifiers(), componentPoint.x,
					componentPoint.y, e.getClickCount(), MouseEventUtil.isRightClick(e)));
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (destComp != null) {
			destComp.dispatchEvent(e);
		}
		return true;
	}
}
