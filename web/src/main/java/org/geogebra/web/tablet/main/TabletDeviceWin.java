package org.geogebra.web.tablet.main;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.WinFileManager;
import org.geogebra.web.touch.gui.dialog.image.ImageInputDialogWin;
import org.geogebra.web.touch.main.TouchDevice;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.gui.browser.BrowseResources;
import org.geogebra.web.web.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.web.gui.util.StandardButton;
import org.geogebra.web.web.main.FileManager;

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

		final BrowseGUI bg = new BrowseGUI(app, button);
		button.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				showOpenPicker(bg);
			}
		});
		return bg;
	}
	
	native void showOpenPicker(BrowseGUI bg) /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			bg.@org.geogebra.web.web.gui.browser.BrowseGUI::showLoading()();
			$wnd.android.callPlugin("OpenDialog", [ 1 ]);
		}
	}-*/;
	

	@Override
	public boolean isOffline(AppW app) {
		return !app.getNetworkOperation().isOnline();
	}

	@Override
	public UploadImageDialog getImageInputDialog(AppW app) {
		return new ImageInputDialogWin(app);
	}
}
