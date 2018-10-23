package org.geogebra.common.jre.headless;

import java.awt.Image;

import org.geogebra.common.jre.gui.MyImageJre;

public interface AppDI {

	void addExternalImage(String name, MyImageJre img);

	void hideDockBarPopup();

	void storeFrameCenter();

	Image getExportImage(double thumbnailPixelsX, double thumbnailPixelsY);

	MyImageJre getExternalImage(String fileName);

}
