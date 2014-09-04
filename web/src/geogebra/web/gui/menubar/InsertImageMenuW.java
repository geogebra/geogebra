package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.html5.gui.util.AppResources;
import geogebra.html5.main.AppW;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.WebCamInputDialog;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Insert Image" menu, part of the "Edit" menu.
 */
public class InsertImageMenuW extends MenuBar {

	/**
	 * Application instance
	 */
	App app;

	/**
	 * Constructs the "Insert Image" menu
	 * @param app Application instance
	 */
	public InsertImageMenuW(AppW app) {

		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
		
		
		MainMenu.addSubmenuArrow(app, this);
		
		
		initActions();
	}

	

	private void initActions() {

	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("File"), true),true,new Command() {
	    	public void execute() {
	    		ImageFileInputDialog dialog = new ImageFileInputDialog((AppW) app, null);
	    		dialog.setVisible(true);
	    	}
	    });

	    /* This causes security exceptions so it will probably be removed (Canvas.toDataURL)
	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("URL")),true,new Command() {
	    	public void execute() {
	    		InputDialogImageURL dialog = new InputDialogImageURL((AppW)app);
	    		dialog.setVisible(true);
	    	}
	    });*/

	    addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("Webcam"), true),true,new Command() {
	    	public void execute() {
	    		WebCamInputDialog dialog = new WebCamInputDialog(false, (AppW) app);
	    		dialog.setVisible(true);
	    	}
	    });
	}
}
