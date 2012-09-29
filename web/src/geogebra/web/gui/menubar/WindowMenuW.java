package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Window" menu.
 */
public class WindowMenuW extends MenuBar{

	private App app;

	/**
	 * Constructs the "Window" menu
	 */
	public WindowMenuW(App application) {

		super(true);
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}
	
	private void initActions(){
		clearItems();
		
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), app.getMenu("")),
		        true, new Command() {
			        public void execute() {
			        	com.google.gwt.user.client.Window.open("http://www.geogebra.org/web/web_gui/", "_blank", "");
			        }
		        });
		
	}
	
}
