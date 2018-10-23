package org.geogebra.desktop.headless;

import java.awt.Image;

import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.desktop.gui.MyImageD;

public interface AppDI {

	void addExternalImage(String name, MyImageD img);

	void hideDockBarPopup();

	void storeFrameCenter();

	Image getExportImage(double thumbnailPixelsX, double thumbnailPixelsY);

	MyImageJre getExternalImage(String fileName);

}
