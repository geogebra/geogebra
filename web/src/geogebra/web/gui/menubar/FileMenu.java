package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class FileMenu extends MenuBar {

	private AbstractApplication app;

	public FileMenu(AbstractApplication app) {
	    super(true);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    initActions();
		update();
    }

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions() {
	    addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("New")),true,new Command() {
			
			public void execute() {
				Window.alert("Soon...");
			}
		});
	    
    }

}
