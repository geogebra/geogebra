package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.dialog.ImageFileInputDialog;
import geogebra.web.gui.dialog.WebCamInputDialog;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
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
		
		if (((AppW) app).getLAF().isSmart()) {
			this.addStyleName("subMenuLeftSide");
			addSubmenuArrow();
		}
		
		initActions();
	}

	private void addSubmenuArrow() {
		FlowPanel arrowSubmenu = new FlowPanel();
		arrowSubmenu.addStyleName("arrowSubmenu");
		Image arrow = new Image(GuiResources.INSTANCE.arrow_submenu_right());
		arrowSubmenu.add(arrow);
	    this.getElement().appendChild(arrowSubmenu.getElement());
    }

	private void initActions() {

	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("File"), true),true,new Command() {
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

	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("Webcam"), true),true,new Command() {
	    	public void execute() {
	    		WebCamInputDialog dialog = new WebCamInputDialog(false, (AppW) app);
	    		dialog.setVisible(true);
	    	}
	    });
	}
}
