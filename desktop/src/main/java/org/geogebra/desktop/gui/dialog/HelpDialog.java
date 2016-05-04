package org.geogebra.desktop.gui.dialog;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.GuiManager.Help;
import org.geogebra.common.kernel.Macro;
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

	public void openToolHelp(int mode) {

		String toolName = app.getToolNameOrHelp(mode, true);
		String helpText = app.getToolNameOrHelp(mode, false);
		ImageIcon icon;
		String modeTextInternal = null;

		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {

			Macro macro = app.getKernel()
					.getMacro(mode - EuclidianConstants.MACRO_MODE_ID_OFFSET);

			String iconName = macro.getIconFileName();
			MyImageD img = ((AppD) app).getExternalImage(iconName);
			Color border = Color.lightGray;

			if (img == null || img.isSVG()) {
				// default icon
				icon = ((AppD) app).getToolBarImage("mode_tool.png", border);
			} else {
				// use image as icon
				icon = new ImageIcon(
						ImageManagerD.addBorder(img.getImage(), border));
			}

		} else {

			modeTextInternal = EuclidianConstants.getModeText(mode);
			icon = ((AppD) app).getToolBarImage(
					"mode_" + modeTextInternal + ".png", Color.BLACK);
		}

		Object[] options = { app.getPlain("ShowOnlineHelp"),
				app.getPlain("Cancel") };
		int n = JOptionPane.showOptionDialog(((AppD) app).getMainComponent(),
				helpText, app.getMenu("ToolHelp") + " - " + toolName,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon,
				options, // the titles of buttons
				options[0]); // default button title

		if (n == 0) {
			if (modeTextInternal == null) {
				// show help for custom tools?
				((GuiManagerD) app.getGuiManager()).openHelp("Custom_Tools",
						Help.GENERIC);
			} else {
				((GuiManagerD) app.getGuiManager()).openHelp(modeTextInternal,
						Help.TOOL);
			}
		}
	}
}
