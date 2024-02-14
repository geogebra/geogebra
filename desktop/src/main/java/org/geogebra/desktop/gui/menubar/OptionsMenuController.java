package org.geogebra.desktop.gui.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.common.util.Util;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

public class OptionsMenuController {

	private RadioButtonMenuBarD menuDecimalPlaces;
	private RadioButtonMenuBarD menuLabeling;
	private final App app;
	Kernel kernel;
	private final RoundingIndexProperty roundingProperty;

	/**
	 * @param app application
	 */
	public OptionsMenuController(App app) {
		this.app = app;
		kernel = app.getKernel();
		this.roundingProperty = new RoundingIndexProperty(app, app.getLocalization());
	}

	private void processRounding(Integer index) {
		roundingProperty.setIndex(index > app.getLocalization().getDecimalPlaces().length
				? index - 1 : index);
	}

	private void processFontSize(Integer index) {
		try {
			app.setFontSize(Util.menuFontSizes(index), true);
			app.setUnsaved();
		} catch (Exception e) {
			app.showError(e.toString());
		}
	}

	private void processLabeling(Integer index) {
		// Labeling
		app.setLabelingStyle(index);
		app.setUnsaved();
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

		menuDecimalPlaces.addRadioButtonMenuItems(this::processRounding,
				strDecimalSpaces, 0, false);

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
		menuLabeling.addRadioButtonMenuItems(this::processLabeling, lstr,
				 0, true);

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

		String[] fsfi = new String[Util.menuFontSizesLength()];

		// find current pos
		int fontSize = app.getFontSize();
		int pos = 0;
		for (int i = 0; i < Util.menuFontSizesLength(); i++) {
			if (fontSize == Util.menuFontSizes(i)) {
				pos = i;
			}
			fsfi[i] = app.getLocalization().getPlain("Apt",
					Util.menuFontSizes(i) + "");
		}

		submenu.addRadioButtonMenuItems(this::processFontSize, fsfi,
				pos, false);
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

		int pos = roundingProperty.getMenuDecimalPosition(kernel, false);

		try {
			menuDecimalPlaces.setSelected(pos);
		} catch (Exception e) {
			//
		}

	}
}
