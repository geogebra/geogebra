package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.tools.ToolCreationDialogW;
import org.geogebra.web.full.gui.dialog.tools.ToolManagerDialogW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Web implementation of ToolsMenu
 */
public class ToolsMenuW extends Submenu {

	private final ExamController examController = GlobalScope.examController;

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
		if (examController.isIdle()) {
			addItem(MainMenu.getMenuBarItem(
					MaterialDesignResources.INSTANCE.tools_customize_black(),
					loc.getMenu("Toolbar.Customize"),
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							getApp().showCustomizeToolbarGUI();
						}
					}));
		}

		addItem(MainMenu.getMenuBarItem(
				MaterialDesignResources.INSTANCE.tools_create_black(),
				loc.getMenu(getApp().isOpenedForMacroEditing() ? "Tool.SaveAs"
						: "Tool.CreateNew"),
				new MenuCommand(getApp()) {

					@Override
					public void doExecute() {
						ToolCreationDialogW toolCreationDialog = new ToolCreationDialogW(
								getApp());
						toolCreationDialog.center();
					}
				}));

		if (examController.isIdle()) {
			addItem(MainMenu
					.getMenuBarItem(
							MaterialDesignResources.INSTANCE.tools_black(),
							loc.getMenu("Tool.Manage"),
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							DialogData data = new DialogData("Tool.Manage", "Close", null);
							ToolManagerDialogW toolManageDialog = new ToolManagerDialogW(
									getApp(), data);
							toolManageDialog.show();
						}
					}));
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
