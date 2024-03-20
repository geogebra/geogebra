package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatSTL;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports STL.
 */
public class DownloadStlAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		app.setExport3D(new FormatSTL());
	}
}
