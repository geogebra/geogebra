package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.dialog.ExerciseBuilderDialog;
import org.geogebra.web.web.gui.dialog.ToolCreationDialog;
import org.geogebra.web.web.gui.dialog.ToolManagerDialogW;

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

		super(true, "tools");
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	/**
	 * Initialize the menu items
	 */
	protected void initActions() {
		Localization loc = app.getLocalization();
		if (!app.isExam()) {
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_tools_customize().getSafeUri().asString(),
					loc.getMenu("Toolbar.Customize"), true), true,
					new MenuCommand(app) {

			@Override
			public void doExecute() {
		        app.showCustomizeToolbarGUI();
			}
		});
		}

			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				.menu_icon_tools_new().getSafeUri().asString(),
				loc.getMenu(app.isToolLoadedFromStorage() ? "Tool.SaveAs"
			                : "Tool.CreateNew"),
					true), true, new MenuCommand(app) {
	
				@Override
				public void doExecute() {
					ToolCreationDialog toolCreationDialog = new ToolCreationDialog(app);
					toolCreationDialog.center();
				}
			});

		addItem(MainMenu.getMenuBarHtml(
				GuiResources.INSTANCE.menu_icon_tools().getSafeUri().asString(),
				loc.getMenu("Tool.Manage"),
					true), true, new MenuCommand(app) {

				@Override
				public void doExecute() {
					ToolManagerDialogW toolManageDialog = new ToolManagerDialogW(app);
					toolManageDialog.center();
				}
			});

		if (!app.isExam()) {
		if (app.has(Feature.EXERCISES)) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_create_exercise().getSafeUri().asString(),
						loc.getMenu("Exercise.CreateNew"), true), true,
			        new MenuCommand(app) {

				        @Override
				        public void doExecute() {
					        ExerciseBuilderDialog exerciseBuilderDialog = new ExerciseBuilderDialog(
					                app);
					        exerciseBuilderDialog.center();
				        }
			        });
		}
		}
	}
	
				 

}


