package geogebra.web.main;

import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.dialog.image.ImageInputDialog;
import geogebra.web.gui.dialog.image.UploadImageDialog;

import com.google.gwt.user.client.Window;

public class BrowserDevice implements GDevice {

	public boolean supportsExport() {
		return true;
	}

	@Override
	public FileManager getFileManager(AppW app) {
		return new FileManagerW(app);
	}

	@Override
	public void copyEVtoClipboard(EuclidianViewW ev) {
		Window.open(ev.getExportImageDataUrl(3, true), "_blank", null);

	}

	@Override
	public void setMinWidth(GeoGebraAppFrame frame) {
		if (Window.getClientWidth() > 760) {
			frame.removeStyleName("minWidth");
			frame.syncPanelSizes();
		} else {
			frame.addStyleName("minWidth");
		}
	}

	@Override
	public boolean isOffline(AppW app) {
		return !app.getNetworkOperation().isOnline();
	}

	public UploadImageDialog getImageInputDialog(AppW app) {

		return new ImageInputDialog(app);
	}

	@Override
	public BrowseGUI getBrowseGUI(AppW app) {
		return new BrowseGUI(app);
	}

}
