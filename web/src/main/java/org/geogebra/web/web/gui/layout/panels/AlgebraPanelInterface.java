package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;

public interface AlgebraPanelInterface {
	void setAlgebraView(final AlgebraViewW av);

	void scrollTo(int position);

	/**
	 * scrolls to the bottom of the panel
	 */
	void scrollToBottom();

	/**
	 * Scroll to the item that is selected.
	 */
	void scrollToActiveItem();

	/**
	 * Saves the current scroll position of the dock panel.
	 */
	void saveScrollPosition();
}
