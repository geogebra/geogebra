package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

public class EditMenu extends MenuBar {
	
	private AbstractApplication app;

	public EditMenu(AbstractApplication app) {

		super(true);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    initActions();
	}

	private void initActions() {
	    addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.edit_copy().getSafeUri().asString(),app.getMenu("CopyImage")),true,new Command() {
	    	public void execute() {
	    		((Application)app).copyEVtoClipboard();
	    	}
	    });
	}
}
