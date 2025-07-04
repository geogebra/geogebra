package org.geogebra.desktop.euclidian.event;

/**
 * interface to merge MouseEventD and Mouse3DEvent
 * 
 * @author mathieu
 *
 */
public interface MouseEventND {

	/**
	 * 
	 * @return component where the event occurred
	 */
	java.awt.Component getComponent();

	/**
	 * @return whether middle click was used.
	 */
	boolean isMiddleClick();

}
