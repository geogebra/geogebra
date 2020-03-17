package org.geogebra.web.html5.gui.accessibility;

import org.geogebra.common.euclidian.EuclidianView;

/**
 * Accessibility adapter for euclidian view
 */
public interface EuclidianViewAccessibiliyAdapter {

	/**
	 * focus speech rec panel
	 * 
	 * @return whether there is a selectable speech panel
	 */
	boolean focusSpeechRecBtn();

	/**
	 * @return view in this dock panel
	 */
	EuclidianView getEuclidianView();

	/**
	 * Focus the settings button if visible
	 *
	 * @return if there was an element to focus to.
	 */
	boolean focusSettings();

	/**
	 * Focuses the reset button.
	 *
	 * @return if the reset button was focused
	 */
	boolean focusResetButton();
}
