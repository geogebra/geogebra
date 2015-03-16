package geogebra.web.gui.menubar;

import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.web.export.AnimationExportDialogW;
import geogebra.web.gui.images.AppResources;

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
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.image_x_generic()
		        .getSafeUri().asString(), app.getPlain("DrawingPadAsPicture"),
		        true), true, new Command() {

			public void execute() {
				String url = ((EuclidianViewW) app.getActiveEuclidianView())
				        .getExportImageDataUrl(1.0, false);
				download(url, "export-png");
			}
		});

		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
		        .getSafeUri().asString(), app.getPlain("AnimatedGIF"),
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

	public static native void download(String url,
	        String title) /*-{

		if ($wnd.navigator.msSaveBlob) {
			//works for chrome and internet explorer
			var image = document.createElement('img');
			image.src = image;

			$wnd.navigator.msSaveBlob(image, title);
		} else {
			//works for firefox
			var a = $doc.createElement("a");
			$doc.body.appendChild(a);
			a.style = "display: none";
			a.href = url;
			a.download = title;
			a.click();
		}

	}-*/;
}
