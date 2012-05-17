package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 * Creates a menubar for GeoGebraWeb
 *
 */
public class GeoGebraMenubar extends MenuBar {
	
		private AbstractApplication app;
		private FileMenu fileMenu;

		public GeoGebraMenubar(AbstractApplication app) {
	        super();
	        this.app = app;
	        init();
	        addStyleName("GeoGebraMenuBar");
	        
        }

		private void init() {

			//file
			fileMenu = new FileMenu(app);
			addItem(app.getMenu("File"),fileMenu);
			MenuItem linktoggb = addItem(getMenuBarHtml(AppResources.INSTANCE.GeoGebraTube().getSafeUri().asString(),""),true, new Command() {
				
				public void execute() {
					Window.open("http://geogebratube.org", "", "");
				}
			});
			linktoggb.setStyleName("linktoggbtube");
			linktoggb.setTitle("Go to GeoGebraTube");
			
			//undo-redo buttons
			MenuItem redoButton = addItem(getMenuBarHtml(AppResources.INSTANCE.edit_redo().getSafeUri().asString(),""),true, new Command() {
				
				public void execute() {
					app.getGuiManager().redo();
				}
			});
			
			MenuItem undoButton = addItem(getMenuBarHtml(AppResources.INSTANCE.edit_undo().getSafeUri().asString(),""),true, new Command() {
				
				public void execute() {
					app.getGuiManager().undo();
				}
			});
			
			redoButton.setStyleName("redoButton");
			redoButton.setTitle("Redo");			
			undoButton.setStyleName("undoButton");
			undoButton.setTitle("Undo");
			
        }
		
		public static String getMenuBarHtml(String url,String text) {
			return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+text;
		}
	
}
