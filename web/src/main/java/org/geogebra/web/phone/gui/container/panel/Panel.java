package org.geogebra.web.phone.gui.container.panel;

import org.geogebra.web.phone.gui.container.ViewContainer;

/**
 * Interface for widgets that act as a main view (e.g. canvas for the euclidian
 * view, table for the CAS view etc.)
 */
public interface Panel extends ViewContainer {

	void updateAfterResize();
	// Add methods if needed
}
