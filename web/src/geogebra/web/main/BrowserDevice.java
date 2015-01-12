package geogebra.web.main;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.dialog.image.ImageInputDialog;
import geogebra.web.gui.dialog.image.UploadImageDialog;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolViewW;

import com.google.gwt.user.client.Window;

public class BrowserDevice implements GDevice {

	public boolean supportsExport() {
		return true;
	}

	@Override
	public FileManager createFileManager(AppW app) {
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
	public BrowseGUI createBrowseView(AppW app) {
		return new BrowseGUI(app);
	}

	@Override
	public ConstructionProtocolView getConstructionProtocolView(AppW app) {
		return new ConstructionProtocolViewW(app);
	}

}
