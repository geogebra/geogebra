package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;

import com.google.gwt.user.client.ui.Widget;

public interface SaveDialogI {

	void show();

	void setTitle();

	void setSaveType(MaterialType type);

	void hide();

	/**
	 * Update the UI to say "discard" rather than "cancel"
	 */
	void setDiscardMode();

	/**
	 * Show the dialog and position relatively to anchor
	 * @param anchor
	 *         for dialog position; pass null to center
	 */
	void showAndPosition(Widget anchor);
}
