package org.geogebra.web.full.gui.openfileview;

import org.geogebra.web.full.gui.browser.MaterialCardController;

/**
 * Common interface for material cards.
 */
public interface MaterialCardI {

	/**
	 * @param visible
	 *            whether this should be shown
	 */
	void setVisible(boolean visible);

	/**
	 * Remove from UI.
	 */
	void remove();

	/**
	 * Show delete dialog.
	 */
	void onDelete();

	/**
	 * @return card title
	 */
	String getCardTitle();

	/**
	 * @return card controller
	 */
	MaterialCardController getController();
}
