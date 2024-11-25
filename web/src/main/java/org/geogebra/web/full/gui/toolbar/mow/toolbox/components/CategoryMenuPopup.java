package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.contextmenu.CalculatorSubMenu;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResourcePrototype;

public class CategoryMenuPopup extends GPopupMenuW implements SetLabels {
	private final List<Integer> tools;

	/**
	 * Menu popup for MOW toolbox
	 * @param appW - application
	 * @param tools - list of tools
	 */
	public CategoryMenuPopup(AppW appW, List<Integer> tools) {
		super(appW);
		this.tools = tools;
		getPopupPanel().setAutoHideEnabled(false);
		buildGui();
	}

	private void buildGui() {
		clearItems();

		for (Integer mode : tools) {
			if (mode == EuclidianConstants.MODE_CALCULATOR) {
				addItem(new AriaMenuItem("GeoGebra", MaterialDesignResources.INSTANCE
						.geogebra_black(), new CalculatorSubMenu(getApp())));
			} else {
				addItem(mode);
			}
		}
	}

	private void addItem(int mode) {
		String text = getTextForMode(mode);

		AriaMenuItem item = MainMenu.getMenuBarItem(
				SVGResourcePrototype.EMPTY, text, () -> getApp().setMode(mode));
		getImageForMode(mode, item);
		addItem(item);
	}

	/**
	 * @param mode - tool mode
	 * @param item popup item
	 */
	public void getImageForMode(int mode, AriaMenuItem item) {
		GGWToolBar.getImageResource(mode, getApp(), item);
	}

	/**
	 * @param mode - tool mode
	 * @return tool name
	 */
	public String getTextForMode(int mode) {
		return getApp().getToolName(mode);
	}

	@Override
	public void setLabels() {
		buildGui();
	}
}
