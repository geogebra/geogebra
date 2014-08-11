package geogebra.web.gui.menubar;

import geogebra.web.export.AnimationExportDialogW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * @author bencze
 * The "Export Image" menu, part of the "File" menu.
 */
public class ExportMenuW extends MenuBar {

	/**
	 * Application instance
	 */
	AppW app;

	/**
	 * Constructs the "Insert Image" menu
	 * 
	 * @param app
	 *            Application instance
	 */
	public ExportMenuW(AppW app) {
		super(true);
		
		this.app = app;
		addStyleName("GeoGebraMenuBar");
		MainMenu.addSubmenuArrow(app, this);
		
		initActions();
	}

	private void initActions() {

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("ExportAnimatedGIF"),
		        true), true, new Command() {
			public void execute() {
				DialogBox dialog = new AnimationExportDialogW(app);
				dialog.center();
				dialog.show();
				// export dialog comes here
				// ImageFileInputDialog dialog = new ImageFileInputDialog((AppW)
				// app, null);
				// dialog.setVisible(true);
			}
		});
	}

}
