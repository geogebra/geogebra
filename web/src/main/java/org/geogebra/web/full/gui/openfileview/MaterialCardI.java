package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.util.AsyncOperation;

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
	 * Call API to rename material.
	 * 
	 * @param title
	 *            new title
	 */
	void rename(String title);

	/**
	 * Update title in UI.
	 * 
	 * @param title
	 *            material title
	 */
	void setMaterialTitle(String title);

	/**
	 * Call API to copy yhe material.
	 */
	void copy();

	/**
	 * Show delete dialog.
	 */
	void onDelete();

	/**
	 * @return title of the material
	 */
	String getMaterialTitle();

	void setShare(String text, boolean booleanValue,
			AsyncOperation<Boolean> callback);

	String getMaterialID();

}
