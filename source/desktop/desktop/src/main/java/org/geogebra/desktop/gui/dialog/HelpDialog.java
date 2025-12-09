/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.dialog;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.ManualPage;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.ImageManagerD;

public class HelpDialog {
	private final AppD app;

	public HelpDialog(AppD app) {
		this.app = app;
	}

	/**
	 * Open tool help for active mode.
	 */
	public void openToolHelp() {
		openToolHelp(app.getMode());
	}

	/**
	 * @param mode app mode
	 */
	public void openToolHelp(int mode) {
		String toolName = app.getToolName(mode);
		String helpText = app.getToolHelp(mode);
		Icon icon;
		String modeTextInternal = null;

		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {

			Macro macro = app.getKernel()
					.getMacro(mode - EuclidianConstants.MACRO_MODE_ID_OFFSET);

			String iconName = macro.getIconFileName();
			MyImageD img = app.getExternalImage(iconName);
			Color border = Color.lightGray;

			if (img == null || img.isSVG()) {
				// default icon
				icon = app.getToolIcon(border);
			} else {
				// use image as icon
				icon = new ImageIcon(
						ImageManagerD.addBorder(img.getImage(), border, null));
			}

		} else {

			modeTextInternal = EuclidianConstants.getModeHelpPage(mode);
			icon = app.getToolBarImage(modeTextInternal, Color.BLACK);
		}
		Localization loc = app.getLocalization();
		Object[] options = { loc.getMenu("ShowOnlineHelp"),
				loc.getMenu("Cancel") };
		int n = JOptionPane.showOptionDialog(app.getMainComponent(), helpText,
				loc.getMenu("ToolHelp") + " - " + toolName,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon,
				options, // the titles of buttons
				options[0]); // default button title

		if (n == 0) {
			GuiManagerD guiManager = (GuiManagerD) app.getGuiManager();
			if (modeTextInternal == null) {
				// show help for custom tools?
				guiManager.openHelp(ManualPage.TOOL, "Custom_Tools");
			} else {
				guiManager.openHelp(ManualPage.TOOL, modeTextInternal);
			}
		}
	}
}
