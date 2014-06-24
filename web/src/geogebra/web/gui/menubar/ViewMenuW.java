package geogebra.web.gui.menubar;

import geogebra.html5.gui.view.Views;
import geogebra.web.gui.images.AppResources;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;

/**
 * The "View" menu for the applet.
 * For application use ViewMenuApplicationW class
 */
public class ViewMenuW extends GMenuBar {

	
	/**
	 * Menuitem with checkbox for show algebra view
	 */
	GCheckBoxMenuItem[] items;
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
	
	protected void initRefreshActions() {

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
	
	
	protected void initActions() {
		items = new GCheckBoxMenuItem[Views.ids.length];
		for(int k = 0; k< Views.ids.length; k++){
			final int i = k;
			if(!app.supportsView(Views.ids[i])){
				continue;
			}
			items[i] = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(Views.icons[i]
			        .getSafeUri().asString(), app.getPlain(Views.keys[i]), true),
			        new Command() {
				        public void execute() {
				        	app.getGuiManager().setShowView(
									!app.getGuiManager().showView(Views.ids[i]), Views.ids[i]);
				        	items[i].setSelected(app.getGuiManager().showView(Views.ids[i]));
				        }}
				      );
			addItem(items[i].getMenuItem());
		}
		
		addSeparator();
		
		initRefreshActions();
		
		update();
	}
	
	public void update(){
		for(int k = 0; k < items.length; k++){
			if(items[k] != null){
				items[k].setSelected(app.getGuiManager().showView(Views.ids[k]));
			}
		}
	}

}
