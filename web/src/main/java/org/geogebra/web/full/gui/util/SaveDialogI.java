package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;

public interface SaveDialogI {

	void show();

	void setTitle();

	void setSaveType(MaterialType type);

	void hide();

	/**
	 * Update the UI to say "discard" rather than "cancel"
	 */
	void setDiscardMode();
}