package geogebra.web.gui.menubar;

import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.dialog.ToolCreationDialog;
import geogebra.web.gui.dialog.ToolManagerDialogW;

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

		super(true);
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	protected void initActions() {

		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_tools_customize().getSafeUri().asString(),
		        app.getMenu("Toolbar.Customize"), true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
		        app.showCustomizeToolbarGUI();
			}
		});

		if(app.isPrerelease()){	
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
			        .menu_icon_tools_new().getSafeUri().asString(), app
			        .getMenu(app.isToolLoadedFromStorage() ? "Tool.SaveAs"
			                : "Tool.CreateNew"),
					true), true, new MenuCommand(app) {
	
				@Override
				public void doExecute() {
					ToolCreationDialog toolCreationDialog = new ToolCreationDialog(app);
					toolCreationDialog.center();
				}
			});
		}

		if(app.isPrerelease()){	
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_tools().getSafeUri().asString(), app.getMenu("Tool.Manage"),
					true), true, new MenuCommand(app) {

				@Override
				public void doExecute() {
					ToolManagerDialogW toolManageDialog = new ToolManagerDialogW(app);
					toolManageDialog.center();
				}
			});
		}
	}
	
				 

}


