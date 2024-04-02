package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import java.util.List;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class CategoryMenuPopup extends GPopupMenuW {

	/**
	 * Menu popup for MOW toolbox
	 * @param appW - application
	 * @param tools - list of tools
	 */
	public CategoryMenuPopup(AppW appW, List<Integer> tools) {
		super(appW);
		buildGui(tools);
	}

	private void buildGui(List<Integer> tools) {
		for (Integer mode : tools) {
			addItem(mode);
		}
	}

	private void addItem(int mode) {
		SVGResource image = (SVGResource) GGWToolBar.getImageURLNotMacro(
				ToolbarSvgResources.INSTANCE, mode, getApp());
		String text = getApp().getToolName(mode);

		AriaMenuItem item = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
				image.getSafeUri().asString(), text), true, () ->
				getApp().setMode(mode));
		addItem(item);
	}

	/**
	 * show popup at position
	 * @param left - left position
	 * @param top - top position
	 */
	public void show(int left, int top) {
		showAtPoint(left, top);
	}
}
