package org.geogebra.desktop.gui.dialog;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.GuiManagerInterface.Help;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.ImageManagerD;

public class HelpDialog {
	private AppD app;

	public HelpDialog(AppD app) {
		this.app = app;
	}

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
			if (modeTextInternal == null) {
				// show help for custom tools?
				((GuiManagerD) app.getGuiManager()).openHelp("Custom_Tools",
						Help.TOOL);
			} else {
				((GuiManagerD) app.getGuiManager()).openHelp(modeTextInternal,
						Help.TOOL);
			}
		}
	}
}
