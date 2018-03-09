package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.ExerciseBuilderDialog;
import org.geogebra.web.full.gui.dialog.ToolCreationDialogW;
import org.geogebra.web.full.gui.dialog.ToolManagerDialogW;
import org.geogebra.web.html5.main.AppW;

/**
 * Web implementation of ToolsMenu
 */
public class ToolsMenuW extends GMenuBar {

	/** Application */
	AppW app;

	/**
	 * Constructs the "Tools" menu
	 * 
	 * @param application
	 *            The App instance
	 */
	public ToolsMenuW(AppW application) {
		super("tools", application);
		this.app = application;
		if (app.isUnbundled()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		initActions();
	}

	/**
	 * Initialize the menu items
	 */
	protected void initActions() {
		Localization loc = app.getLocalization();
		if (!app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					app.isUnbundled() ? MaterialDesignResources.INSTANCE
							.tools_customize_black().getSafeUri().asString()
							: GuiResources.INSTANCE.menu_icon_tools_customize()
									.getSafeUri().asString(),
					loc.getMenu("Toolbar.Customize"), true), true,
					new MenuCommand(app) {

						@Override
						public void doExecute() {
							app.showCustomizeToolbarGUI();
						}
					});
		}

		addItem(MainMenu.getMenuBarHtml(
				app.isUnbundled()
						? MaterialDesignResources.INSTANCE.tools_create_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE.menu_icon_tools_new()
								.getSafeUri().asString(),
				loc.getMenu(app.isToolLoadedFromStorage() ? "Tool.SaveAs"
						: "Tool.CreateNew"),
				true), true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						ToolCreationDialogW toolCreationDialog = new ToolCreationDialogW(
								app);
						toolCreationDialog.center();
					}
				});

		addItem(MainMenu
				.getMenuBarHtml(
						app.isUnbundled()
								? MaterialDesignResources.INSTANCE.tools_black()
										.getSafeUri().asString()
								: GuiResources.INSTANCE.menu_icon_tools()
										.getSafeUri().asString(),
						loc.getMenu("Tool.Manage"), true),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						ToolManagerDialogW toolManageDialog = new ToolManagerDialogW(
								app);
						toolManageDialog.center();
					}
				});

		if (!app.isExam()) {
			if (app.has(Feature.EXERCISES)) {
				addItem(MainMenu.getMenuBarHtml(
						app.isUnbundled() ? MaterialDesignResources.INSTANCE
								.new_exercise_black().getSafeUri().asString()
								: GuiResources.INSTANCE.menu_create_exercise()
										.getSafeUri().asString(),
						loc.getMenu("Exercise.CreateNew"), true), true,
						new MenuCommand(app) {

							@Override
							public void doExecute() {
								openExerciseBuilder();
							}
						});
			}
		}
	}

	protected void openExerciseBuilder() {
		ExerciseBuilderDialog exerciseBuilderDialog = new ExerciseBuilderDialog(
				app);
		exerciseBuilderDialog.center();
	}

}
