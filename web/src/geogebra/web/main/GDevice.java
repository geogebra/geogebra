package geogebra.web.main;

import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.image.UploadImageDialog;

public interface GDevice  {
	public FileManager getFileManager(AppW app);
	void copyEVtoClipboard(EuclidianViewW euclidianView1);
	public GuiManagerInterfaceW newGuiManager(AppW appWapplication);
	public void setMinWidth(GeoGebraAppFrame frame);
	public boolean isOffline(AppW app);
	public UploadImageDialog getImageInputDialog(AppW app);
}
