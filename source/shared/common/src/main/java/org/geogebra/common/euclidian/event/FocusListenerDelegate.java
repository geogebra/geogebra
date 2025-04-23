package org.geogebra.common.euclidian.event;

/**
 * Focus listener for autocomple text inputs.
 * Used as a delegate by platform-dependent focus listeners.
 */
public interface FocusListenerDelegate {

	void focusLost();

	void focusGained();

}
