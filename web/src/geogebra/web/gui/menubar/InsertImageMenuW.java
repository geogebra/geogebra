package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.WebCamInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

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
	public InsertImageMenuW(App app) {

		super(true);
		this.app = app;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}

	private void initActions() {

	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("File")),true,new Command() {
	    	public void execute() {
	    		ImageFileInputDialog dialog = new ImageFileInputDialog((AppW) app, null);
	    		dialog.setVisible(true);
	    	}
	    });

	    /* This causes security exceptions so it will probably be removed (Canvas.toDataURL)
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("URL")),true,new Command() {
	    	public void execute() {
	    		InputDialogImageURL dialog = new InputDialogImageURL((AppW)app);
	    		dialog.setVisible(true);
	    	}
	    });*/

	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("Webcam")),true,new Command() {
	    	public void execute() {
	    		WebCamInputDialog dialog = new WebCamInputDialog(false, (AppW) app);
	    		dialog.setVisible(true);
	    	}
	    });
	}
}
