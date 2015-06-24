package org.geogebra.common.gui.menubar;

import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;

/**
 * This class is not a superclass of OptionsMenu, only common method stack
 */
public class OptionsMenu {

	private RadioButtonMenuBar menuAlgebraStyle;
	private RadioButtonMenuBar menuDecimalPlaces;
	private RadioButtonMenuBar menuLabeling;
	private App app;
	private Kernel kernel;
	private MenuFactory menuFactory;

	public OptionsMenu(App app, MenuFactory menuFactory) {
		this.app = app;
		kernel = app.getKernel();
		this.menuFactory = menuFactory;
	}

	public void processActionPerformed(String cmd) {
		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);

				kernel.setPrintDecimals(decimals);
				kernel.updateConstruction();
				app.refreshViews();

				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				e.printStackTrace();
				app.showError(e.toString());
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);
				// Application.debug("figures " + figures);

				kernel.setPrintFigures(figures);
				kernel.updateConstruction();
				app.refreshViews();

				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// font size
		else if (cmd.endsWith("pt")) {
			try {
				app.setFontSize(Integer.parseInt(cmd.substring(0, 2)));
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

	public RadioButtonMenuBar newSubmenu() {
		return this.menuFactory.newSubmenu();
	}

	/**
	 * Adds the "Algebra description" menu for the menu given in parameter
	 * 
	 * @param menu
	 *            "Algebra description menu will be added for this
	 */
	public void addAlgebraDescriptionMenu(MenuInterface menu) {
		menuAlgebraStyle = newSubmenu();

		String[] strDescription = { app.getPlain("Value"),
				app.getPlain("Definition"), app.getPlain("Command") };
		String[] strDescriptionAC = { "0", "1", "2" };

		menuAlgebraStyle.addRadioButtonMenuItems(new MyActionListener() {
			public void actionPerformed(String command) {
				int desc = Integer.parseInt(command);
				kernel.setAlgebraStyle(desc);
				kernel.updateConstruction();
			}
		}, strDescription, strDescriptionAC, kernel.getAlgebraStyle(), false);
		app.addMenuItem(menu, app.getEmptyIconFileName(),
				app.getMenu("AlgebraDescriptions"), true, menuAlgebraStyle);

		updateMenuViewDescription();
	}

	/**
	 * Update algebra style description (switch between value / definition /
	 * command).
	 */
	public void updateMenuViewDescription() {
		if (menuAlgebraStyle != null) {
			menuAlgebraStyle.setSelected(kernel.getAlgebraStyle());
		}
	}

	/**
	 * Update the menu with all decimal places.
	 */
	public void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null)
			return;
		int pos = -1;

		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < App.figuresLookup.length)
				pos = App.figuresLookup[figures];
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals >= 0 && decimals < App.decimalsLookup.length)
				pos = App.decimalsLookup[decimals];

		}

		try {
			menuDecimalPlaces.setSelected(pos);
		} catch (Exception e) {
			//
		}

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
				strDecimalSpaces, App.strDecimalSpacesAC, 0, false);

		app.addMenuItem(menu, app.getEmptyIconFileName(),
				app.getMenu("Rounding"), true, menuDecimalPlaces);

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

		app.addMenuItem(menu, "mode_showhidelabel_16.gif",
				app.getMenu("Labeling"), true, menuLabeling);

		updateMenuLabeling();
	}

	/**
	 * Update the selected item in the labeling capturing menu.
	 */
	public void updateMenuLabeling() {
		if (menuLabeling == null)
			return;

		int pos = app.getLabelingStyleForMenu();
		menuLabeling.setSelected(pos);
	}

	public void addFontSizeMenu(MenuInterface menu) {
		RadioButtonMenuBar submenu = newSubmenu();

		// String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt",
		// "24 pt",
		// "28 pt", "32 pt" };
		String[] fsfi = new String[MyXMLHandler.menuFontSizes.length];
		String[] fontActionCommands = new String[MyXMLHandler.menuFontSizes.length];

		// find current pos
		int fontSize = app.getFontSize();
		int pos = 0;
		for (int i = 0; i < MyXMLHandler.menuFontSizes.length; i++) {
			if (fontSize == MyXMLHandler.menuFontSizes[i]) {
				pos = i;
			}
			fsfi[i] = app.getLocalization().getPlain("Apt",
					MyXMLHandler.menuFontSizes[i] + "");
			fontActionCommands[i] = MyXMLHandler.menuFontSizes[i] + " pt";
		}

		submenu.addRadioButtonMenuItems((MyActionListener) menu, fsfi,
				fontActionCommands, pos, false);
		app.addMenuItem(menu, "font.png", app.getMenu("FontSize"), true,
				submenu);
	}

	public void update() {
		updateMenuDecimalPlaces();
		updateMenuViewDescription();
		updateMenuLabeling();
	}
}
