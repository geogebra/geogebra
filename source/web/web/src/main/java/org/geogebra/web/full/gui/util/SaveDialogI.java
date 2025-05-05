package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;

/**
 * Save dialog.
 */
public interface SaveDialogI {

	/**
	 * Show the dialog, centered.
	 */
	void show();

	/**
	 * Set material type for saving.
	 * @param type material type (ggb, ggs, ggsTemplate)
	 */
	void setSaveType(MaterialType type);

	/**
	 * Hide the dialog.
	 */
	void hide();

	/**
	 * @return whether the dialog is showing
	 */
	boolean isShowing();

	/**
	 * Update the UI to say "discard" rather than "cancel"
	 */
	void setDiscardMode();
}