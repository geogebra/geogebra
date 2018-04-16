package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.ExerciseBuilderDialog;
import org.geogebra.web.full.gui.dialog.ToolCreationDialogW;
import org.geogebra.web.full.gui.dialog.ToolManagerDialogW;
import org.geogebra.web.html5.main.AppW;

/**
 * Web implementation of ToolsMenu
 */
public class ToolsMenuW extends GMenuBar {

	/**
	 * Constructs the "Tools" menu
	 * 
	 * @param application
	 *            The App instance
	 */
	public ToolsMenuW(AppW application) {
		super("tools", application);
		if (application.isUnbundled()) {
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
		Localization loc = getApp().getLocalization();
		if (!getApp().isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.tools_customize_black()
							.getSafeUri().asString(),
					loc.getMenu("Toolbar.Customize"), true), true,
					new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							getApp().showCustomizeToolbarGUI();
						}
					});
		}

		addItem(MainMenu.getMenuBarHtml(
				MaterialDesignResources.INSTANCE.tools_create_black()
								.getSafeUri().asString(),
				loc.getMenu(getApp().isToolLoadedFromStorage() ? "Tool.SaveAs"
						: "Tool.CreateNew"),
				true), true, new MenuCommand(getApp()) {

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
							MaterialDesignResources.INSTANCE.tools_black()
									.getSafeUri().asString(),
							loc.getMenu("Tool.Manage"), true),
					true, new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							ToolManagerDialogW toolManageDialog = new ToolManagerDialogW(
									getApp());
							toolManageDialog.center();
						}
					});

			if (getApp().has(Feature.EXERCISES)) {
				addItem(MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.new_exercise_black()
								.getSafeUri().asString(),
						loc.getMenu("Exercise.CreateNew"), true), true,
						new MenuCommand(getApp()) {

							@Override
							public void doExecute() {
								openExerciseBuilder();
							}
						});
			}
		}
	}

	/**
	 * Open exercise dialog
	 */
	protected void openExerciseBuilder() {
		ExerciseBuilderDialog exerciseBuilderDialog = new ExerciseBuilderDialog(
				getApp());
		exerciseBuilderDialog.center();
	}

}
