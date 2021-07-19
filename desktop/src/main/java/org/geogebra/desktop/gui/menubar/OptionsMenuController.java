package org.geogebra.desktop.gui.menubar;

import org.geogebra.common.gui.menubar.MenuFactory;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Util;

public class OptionsMenuController {

	private RadioButtonMenuBar menuDecimalPlaces;
	private RadioButtonMenuBar menuLabeling;
	private App app;
	Kernel kernel;
	private MenuFactory menuFactory;
	private OptionsMenu optionsMenu;

	public OptionsMenuController(App app, MenuFactory menuFactory) {
		this.app = app;
		kernel = app.getKernel();
		this.menuFactory = menuFactory;
		this.optionsMenu = new OptionsMenu(app.getLocalization());
	}

	public void processActionPerformed(String cmd) {
		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);

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
				// Application.debug("figures " + figures);

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
	public RadioButtonMenuBar newSubmenu() {
		return this.menuFactory.newSubmenu();
	}

	public void addDecimalPlacesMenu(MenuInterface menu) {
		menuDecimalPlaces = newSubmenu();

		/*
		 * int max_dec = 15; String[] strDecimalSpaces = new String[max_dec +
		 * 1]; String[] strDecimalSpacesAC = new String[max_dec + 1]; for (int
		 * i=0; i <= max_dec; i++){ strDecimalSpaces[i] = Integer.toString(i);
		 * strDecimalSpacesAC[i] = i + " decimals"; }
		 */
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenu();

		menuDecimalPlaces.addRadioButtonMenuItems((MyActionListener) menu,
				strDecimalSpaces, App.getStrDecimalSpacesAC(), 0, false);

		menuFactory.addMenuItem(menu, "Rounding", true, menuDecimalPlaces);

		updateMenuDecimalPlaces();
	}

	public void addLabelingMenu(MenuInterface menu) {
		menuLabeling = newSubmenu();

		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off",
				"Labeling.pointsOnly" };
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling",
				"3_labeling" };
		menuLabeling.addRadioButtonMenuItems((MyActionListener) menu, lstr,
				lastr, 0, true);

		menuFactory.addMenuItem(menu, "Labeling", true, menuLabeling);

		updateMenuLabeling();
	}

	/**
	 * Update the selected item in the labeling capturing menu.
	 */
	public void updateMenuLabeling() {
		if (menuLabeling == null) {
			return;
		}

		int pos = app.getLabelingStyleForMenu();
		menuLabeling.setSelected(pos);
	}

	public void addFontSizeMenu(MenuInterface menu) {
		RadioButtonMenuBar submenu = newSubmenu();

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

		submenu.addRadioButtonMenuItems((MyActionListener) menu, fsfi,
				fontActionCommands, pos, false);
		menuFactory.addMenuItem(menu, "FontSize", true, submenu);
	}

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
