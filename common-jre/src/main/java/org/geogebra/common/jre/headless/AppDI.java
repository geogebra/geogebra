package org.geogebra.common.jre.headless;

import org.geogebra.common.jre.gui.MyImageJre;

public interface AppDI {

	void addExternalImage(String name, MyImageJre img);

	MyImageJre getExportImage(double thumbnailPixelsX, double thumbnailPixelsY);

	MyImageJre getExternalImage(String fileName);

}
