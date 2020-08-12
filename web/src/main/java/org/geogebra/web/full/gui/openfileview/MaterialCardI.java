package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.dialog.RenameCard;

/**
 * Common interface for material cards.
 */
public interface MaterialCardI extends RenameCard {

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
	 * Sets the title of the material card.
	 *
	 * @param title to set.
	 */
	void setCardTitle(String title);

	/**
	 * Show delete dialog.
	 */
	void onDelete();

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
