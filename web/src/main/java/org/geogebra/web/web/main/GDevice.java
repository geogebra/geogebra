package org.geogebra.web.web.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.dialog.image.UploadImageDialog;

public interface GDevice {

	public FileManager createFileManager(AppW app);

	void copyEVtoClipboard(EuclidianViewWInterface euclidianViewInterfaceCommon);

	public void setMinWidth(GeoGebraAppFrame frame);

	public boolean isOffline(AppW app);

	public UploadImageDialog getImageInputDialog(AppW app);

	public BrowseViewI createBrowseView(AppW app);

	public ConstructionProtocolView getConstructionProtocolView(AppW app);
}
