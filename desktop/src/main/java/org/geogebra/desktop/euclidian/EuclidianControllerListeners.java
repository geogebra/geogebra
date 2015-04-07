package org.geogebra.desktop.euclidian;

import java.awt.Component;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * interface to add listeners
 * 
 * @author mathieu
 *
 */
public interface EuclidianControllerListeners extends MouseListener,
		MouseMotionListener, MouseWheelListener, ComponentListener {

	/**
	 * add listeners to the panel
	 * 
	 * @param evjpanel
	 *            panel
	 */
	public void addListenersTo(Component evjpanel);
}
