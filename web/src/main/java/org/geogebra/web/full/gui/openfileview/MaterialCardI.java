package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.dialog.CardInfoI;

/**
 * Common interface for material cards.
 */
public interface MaterialCardI extends CardInfoI {

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

	/**
	 * @param groupID
	 *            group to share with
	 * @param share
	 *            whether to share
	 * @param callback
	 *            callback
	 */
	void setShare(String groupID, boolean share,
			AsyncOperation<Boolean> callback);

	/**
	 * @return material id / sharing key
	 */
	String getMaterialID();

	/**
	 * @param visibility
	 *            material visibility
	 */
	void updateVisibility(String visibility);

}
