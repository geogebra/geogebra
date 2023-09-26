package org.geogebra.desktop.gui.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.util.Util;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

public class OptionsMenuController {

	private RadioButtonMenuBarD menuDecimalPlaces;
	private RadioButtonMenuBarD menuLabeling;
	private final App app;
	Kernel kernel;
	private final OptionsMenu optionsMenu;

	/**
	 * @param app application
	 */
	public OptionsMenuController(App app) {
		this.app = app;
		kernel = app.getKernel();
		this.optionsMenu = new OptionsMenu(app.getLocalization());
	}

	/**
	 * @param cmd command
	 */
	public void processActionPerformed(String cmd) {
		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);

				kernel.setPrintDecimals(decimals);
				kernel.updateConstruction(false);
				app.refreshViews();

				// see ticket 79
				kernel.updateConstruction(false);

				app.setUnsaved();
			} catch (Exception e) {
				app.showGenericError(e);
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);

				kernel.setPrintFigures(figures);
				kernel.updateConstruction(false);
				app.refreshViews();

				// see ticket 79
				kernel.updateConstruction(false);

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// font size
		else if (cmd.endsWith("pt")) {
			try {
				app.setFontSize(Integer.parseInt(cmd.substring(0, 2)), true);
				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {
			int mode = Integer.parseInt(cmd.substring(0, 1));
			app.getEuclidianView1().setPointCapturing(mode);
			if (app.hasEuclidianView2EitherShowingOrNot(1)) {
				app.getEuclidianView2(1).setPointCapturing(mode);
			}
			app.setUnsaved();
		}

		// Labeling
		else if (cmd.endsWith("labeling")) {
			int style = Integer.parseInt(cmd.substring(0, 1));
			app.setLabelingStyle(style);
			app.setUnsaved();
		}
	}

	/**
	 * @return newSubmenu
	 */
	public RadioButtonMenuBarD newSubmenu() {
		return new RadioButtonMenuBarD(app);
	}

	/**
	 * Add decimal places menu
	 * @param menu options menu
	 */
	public void addDecimalPlacesMenu(OptionsMenuD menu) {
		menuDecimalPlaces = newSubmenu();
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenu();

		menuDecimalPlaces.addRadioButtonMenuItems(menu,
				strDecimalSpaces, App.getStrDecimalSpacesAC(), 0, false);

		addMenuItem(menu, "Rounding", menuDecimalPlaces);

		updateMenuDecimalPlaces();
	}

	/**
	 * Add labeling menu
	 * @param menu options menu
	 */
	public void addLabelingMenu(OptionsMenuD menu) {
		menuLabeling = newSubmenu();

		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off",
				"Labeling.pointsOnly" };
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling",
				"3_labeling" };
		menuLabeling.addRadioButtonMenuItems(menu, lstr,
				lastr, 0, true);

		addMenuItem(menu, "Labeling", menuLabeling);

		updateMenuLabeling();
	}

	/**
	 * Update the selected item in the labeling capturing menu.
	 */
	public void updateMenuLabeling() {
		if (menuLabeling == null) {
			return;
		}

		LabelVisibility labelVisibility =
				app.getSettings().getLabelSettings().getLabelVisibilityForMenu();
		menuLabeling.setSelected(labelVisibility.getValue());
	}

	/**
	 * Add font size menu
	 * @param menu options menu
	 */
	public void addFontSizeMenu(OptionsMenuD menu) {
		RadioButtonMenuBarD submenu = newSubmenu();

		// String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt",
		// "24 pt",
		// "28 pt", "32 pt" };
		String[] fsfi = new String[Util.menuFontSizesLength()];
		String[] fontActionCommands = new String[Util.menuFontSizesLength()];

		// find current pos
		int fontSize = app.getFontSize();
		int pos = 0;
		for (int i = 0; i < Util.menuFontSizesLength(); i++) {
			if (fontSize == Util.menuFontSizes(i)) {
				pos = i;
			}
			fsfi[i] = app.getLocalization().getPlain("Apt",
					Util.menuFontSizes(i) + "");
			fontActionCommands[i] = Util.menuFontSizes(i) + " pt";
		}

		submenu.addRadioButtonMenuItems(menu, fsfi,
				fontActionCommands, pos, false);
		addMenuItem(menu, "FontSize", submenu);
	}

	private void addMenuItem(MenuInterface parentMenu, String key,
			MenuInterface subMenu) {
		ImageResourceD res = null;
		if ("Labeling".equals(key)) {
			res = GuiResourcesD.MODE_SHOWHIDELABEL;
		}
		if ("FontSize".equals(key)) {
			res = GuiResourcesD.FONT;
		}
		if (res != null) {
			((JMenuItem) subMenu).setIcon(((AppD) app).getMenuIcon(res));
		}
		((JMenuItem) subMenu).setText(app.getLocalization().getMenu(key));
		((JMenu) parentMenu).add((JMenuItem) subMenu);

	}

	/**
	 * Update the menu
	 */
	public void update() {
		updateMenuDecimalPlaces();
		// updateMenuViewDescription();
		updateMenuLabeling();
	}

	/**
	 * Update the menu with all decimal places.
	 */
	public void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null) {
			return;
		}

		int pos = optionsMenu.getMenuDecimalPosition(kernel, false);

		try {
			menuDecimalPlaces.setSelected(pos);
		} catch (Exception e) {
			//
		}

	}
}
