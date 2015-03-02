package geogebra.web.main;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.html5.euclidian.EuclidianViewWInterface;
import geogebra.html5.gui.view.browser.BrowseViewI;
import geogebra.html5.main.AppW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.image.UploadImageDialog;

public interface GDevice {

	public FileManager createFileManager(AppW app);

	void copyEVtoClipboard(EuclidianViewWInterface euclidianViewInterfaceCommon);

	public void setMinWidth(GeoGebraAppFrame frame);

	public boolean isOffline(AppW app);

	public UploadImageDialog getImageInputDialog(AppW app);

	public BrowseViewI createBrowseView(AppW app);

	public ConstructionProtocolView getConstructionProtocolView(AppW app);
}
