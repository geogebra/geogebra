package org.geogebra.common.euclidian.event;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Focus listener for autocomplete text inputs.
 * Used as a delegate by platform-dependent focus listeners.
 */
public interface FocusListenerDelegate {

	@MissingDoc
	void focusLost();

	@MissingDoc
	void focusGained();

}
