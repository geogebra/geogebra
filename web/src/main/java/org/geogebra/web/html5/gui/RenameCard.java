package org.geogebra.web.html5.gui;

public interface RenameCard {

	/**
	 * Call API to rename material.
	 *
	 * @param title
	 *            new title
	 */
	void rename(String title);

	/**
	 * Sets the title of the material card.
	 *
	 * @param title to set.
	 */
	void setCardTitle(String title);

	/**
	 *
	 * @return the title of the card.
	 */
	String getCardTitle();
}
