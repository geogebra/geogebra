package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

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
			addItem(mode);
		}
	}

	private void addItem(int mode) {
		SVGResource image = getImageForMode(mode);
		String text = getTextForMode(mode);

		AriaMenuItem item = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
				image.getSafeUri().asString(), text), true, () -> getApp().setMode(mode));
		addItem(item);
	}

	/**
	 * @param mode - tool mode
	 * @return image of tool
	 */
	public SVGResource getImageForMode(int mode) {
		return (SVGResource) GGWToolBar.getImageURLNotMacro(
				ToolbarSvgResources.INSTANCE, mode, getApp());
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
