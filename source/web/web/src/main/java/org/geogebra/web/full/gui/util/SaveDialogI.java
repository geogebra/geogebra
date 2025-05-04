package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;

/**
 * Save dialog.
 */
public interface SaveDialogI {

	void show();

	void setTitle();

	/**
	 * Set material type for saving.
	 * @param type material type (ggb, ggs, ggsTemplate)
	 */
	void setSaveType(MaterialType type);

	void hide();

	boolean isShowing();

	/**
	 * Update the UI to say "discard" rather than "cancel"
	 */
	void setDiscardMode();
}