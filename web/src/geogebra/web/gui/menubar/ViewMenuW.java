package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Window" menu.
 */
public class ViewMenuW extends MenuBar{

	private App app;

	/**
	 * Constructs the "Window" menu
	 */
	public ViewMenuW(App application) {

		super(true);
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}
	
	private void initActions(){
		clearItems();
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("Refresh")),
		        true, new Command() {
			        public void execute() {
			        	app.refreshViews();
			        }
		        });
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("RecomputeAllViews")),
		        true, new Command() {
			        public void execute() {
			        	app.getKernel().updateConstruction();
			        }
		        });
		
	}
	
}
