package geogebra.tablet.main;

import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.main.AppW;
import geogebra.touch.WinFileManager;
import geogebra.touch.main.TouchDevice;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.browser.BrowseResources;
import geogebra.web.gui.dialog.image.ImageInputDialogWin;
import geogebra.web.gui.dialog.image.UploadImageDialog;
import geogebra.web.gui.util.StandardButton;
import geogebra.web.main.FileManager;

import com.google.gwt.user.client.ui.Widget;

public class TabletDeviceWin extends TouchDevice {

	@Override
	public FileManager createFileManager(AppW app) {
		return new WinFileManager(app);
	}

	@Override
	public BrowseGUI createBrowseView(AppW app) {
		StandardButton button = new StandardButton(
		        BrowseResources.INSTANCE.location_local());
		button.addFastClickHandler(new FastClickHandler() {

			@Override
			public native void onClick(Widget source) /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin("OpenDialog", [ 1 ]);
		}
	}-*/;
		});
		return new BrowseGUI(app, button);
	}

	@Override
	public boolean isOffline(AppW app) {
		return !app.getNetworkOperation().isOnline();
	}

	@Override
	public UploadImageDialog getImageInputDialog(AppW app) {
		return new ImageInputDialogWin(app);
	}
}
