package geogebra.web.gui.menubar;

import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.dialog.ToolCreationDialog;

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
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_tools_new().getSafeUri().asString(), app.getMenu("Tool.CreateNew"),
					true), true, new MenuCommand(app) {
	
				@Override
				public void doExecute() {
					ToolCreationDialog toolCreationDialog = new ToolCreationDialog(app);
					toolCreationDialog.center();
				}
			});
		}
		/*
		addItem(MainMenu.getMenuBarHtml(noIcon, app.getMenu("Tool.Manage"),
		        true), true, new Command() {

			public void execute() {
				// TODO Auto-generated method stub
				App.debug("unimplemented Manage");
			}
		});
		 */

	}

}
