package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.dialog.WebCamInputDialog;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.Application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Insert Image" menu, part of the "Edit" menu.
 */
public class InsertImageMenu extends MenuBar {

	/**
	 * Application instance
	 */
	AbstractApplication app;

	/**
	 * Constructs the "Insert Image" menu
	 * @param app Application instance
	 */
	public InsertImageMenu(AbstractApplication app) {

		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	private void initActions() {

	    addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("File")),true,new Command() {
	    	public void execute() {
	    		ImageFileInputDialog dialog = new ImageFileInputDialog((Application) app, null);
	    		dialog.setVisible(true);
	    	}
	    });

	    addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.users().getSafeUri().asString(),app.getMenu("WebCam")),true,new Command() {
	    	public void execute() {
	    		WebCamInputDialog dialog = new WebCamInputDialog(false, (Application) app);
	    		dialog.setVisible(true);
	    	}
	    });
	}
}
