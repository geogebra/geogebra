package geogebra.web.gui.menubar;

import geogebra.html5.main.AppW;
import geogebra.web.gui.dialog.ToolCreationDialog;
import geogebra.web.gui.images.AppResources;

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

		String noIcon = AppResources.INSTANCE.empty().getSafeUri().asString();

		//if (GeoGebraConstants.IS_PRE_RELEASE) {
		addItem(MainMenu.getMenuBarHtml(noIcon,
		        app.getMenu("Toolbar.Customize"), true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
		        app.showCustomizeToolbarGUI();
			}
		});

		//}

		
		addSeparator();

		addItem(MainMenu.getMenuBarHtml(noIcon, app.getMenu("Tool.CreateNew"),
				true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
				ToolCreationDialog toolCreationDialog = new ToolCreationDialog(app);
				toolCreationDialog.center();
			}
		});
		
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
