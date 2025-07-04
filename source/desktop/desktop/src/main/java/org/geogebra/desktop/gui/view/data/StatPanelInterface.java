package org.geogebra.desktop.gui.view.data;

import java.awt.Font;

import org.geogebra.common.gui.SetLabels;

public interface StatPanelInterface extends SetLabels {

	/**
	 * Update fonts.
	 * @param font app font
	 */
	void updateFonts(Font font);

	/**
	 * Update the panel.
	 */
	void updatePanel();

}
