package org.geogebra.web.full.gui.dialog;

public interface CardInfoI {

	/**
	 * Call API to rename material.
	 *
	 * @param title
	 *            new title
	 */
	void rename(String title);

	/**
	 *
	 * @return the title of the card.
	 */
	String getCardTitle();
}
