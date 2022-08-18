package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;

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
	 * Call API to copy yhe material.
	 */
	void copy();

	/**
	 * Show delete dialog.
	 */
	void onDelete();

	/**
	 * @return material id / sharing key
	 */
	String getMaterialID();

	/**
	 * @param material
	 *            material
	 */
	void updateVisibility(Material material);

	/**
	 * Change name on card and rename via API
	 * @param text new name
	 */
	void rename(String text);

	String getCardTitle();
}
