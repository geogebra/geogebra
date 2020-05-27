package org.geogebra.web.tablet.main;

import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.browser.BrowseResources;
import org.geogebra.web.full.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.full.main.FileManager;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.WinFileManager;
import org.geogebra.web.touch.gui.dialog.image.ImageInputDialogWin;
import org.geogebra.web.touch.main.TouchDevice;

import com.google.gwt.user.client.ui.Widget;

public class TabletDeviceWin extends TouchDevice {
	
	public TabletDeviceWin() {
		assumeTouchEvents();
	}

	private native void assumeTouchEvents()/*-{
		$wnd.mqTouch
	}-*/;

	@Override
	public FileManager createFileManager(AppW app) {
		return new WinFileManager(app);
	}

	@Override
	public BrowseGUI createBrowseView(AppW app) {
		StandardButton button = new StandardButton(
				BrowseResources.INSTANCE.location_local(), app);

		final BrowseGUI bg = new BrowseGUI(app, button);
		button.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				showOpenPicker(bg);
			}
		});
		return bg;
	}

	/**
	 * @param bg
	 *            browse GUI
	 */
	native void showOpenPicker(BrowseGUI bg) /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			bg.@org.geogebra.web.full.gui.browser.BrowseGUI::showLoading()();
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

	@Override
	public native void resizeView(int width, int height) /*-{
		if ($wnd.android && $wnd.android.callPlugin) {
			$wnd.android.callPlugin("ResizeView", [ width, height ]);
		}
	}-*/;
}
