package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.ToolCreationDialogW;
import org.geogebra.web.full.gui.dialog.ToolManagerDialogW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Web implementation of ToolsMenu
 */
public class ToolsMenuW extends Submenu {

	/**
	 * Constructs the "Tools" menu
	 *
	 * @param application
	 *            The App instance
	 */
	public ToolsMenuW(AppW application) {
		super("tools", application);
		addExpandableStyleWithColor(false);
		initActions();
	}

	/**
	 * Initialize the menu items
	 */
	protected void initActions() {
		Localization loc = getApp().getLocalization();
		if (!getApp().isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.tools_customize_black(),
					loc.getMenu("Toolbar.Customize")), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							getApp().showCustomizeToolbarGUI();
						}
					});
		}

		addItem(MainMenu.getMenuBarHtml(
				MaterialDesignResources.INSTANCE.tools_create_black(),
				loc.getMenu(getApp().isToolLoadedFromStorage() ? "Tool.SaveAs"
						: "Tool.CreateNew")),
				true, new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						ToolCreationDialogW toolCreationDialog = new ToolCreationDialogW(
								getApp());
						toolCreationDialog.center();
					}
				});

		if (!getApp().isExam()) {
			addItem(MainMenu
					.getMenuBarHtml(
							MaterialDesignResources.INSTANCE.tools_black(),
							loc.getMenu("Tool.Manage")),
					true, new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							ToolManagerDialogW toolManageDialog = new ToolManagerDialogW(
									getApp());
							toolManageDialog.center();
						}
					});
		}
	}

	@Override
	public SVGResource getImage() {
		return MaterialDesignResources.INSTANCE.tools_black();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "Tools";
	}

}
