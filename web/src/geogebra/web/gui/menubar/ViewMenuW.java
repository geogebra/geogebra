package geogebra.web.gui.menubar;

import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * The "View" menu for the applet.
 * For application use ViewMenuApplicationW class
 */
public class ViewMenuW extends GMenuBar {

	/**
	 * Application instance
	 */
	AppW app;
	
	/**
	 * Constructs the "View" menu
	 * 
	 * @param application
	 *            The App instance
	 */
	public ViewMenuW(AppW application) {

		super(true);
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}
	
	protected void initActions() {

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("Refresh"), true), true,
		        new Command() {
			        public void execute() {
				        app.refreshViews();
			        }
		        });

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getMenu("RecomputeAllViews"), true),
		        true, new Command() {
			        public void execute() {
				        app.getKernel().updateConstruction();
			        }
		        });

	}
	

}
