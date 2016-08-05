package org.geogebra.web.web.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASTableControllerW;
import org.geogebra.web.web.cas.view.CASTableW;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;

public abstract class LaTeXHelper {
	public abstract void initialize();

	public abstract CASTableCellEditor getCASEditor(CASTableW table, AppW app,
			CASTableControllerW ml);

	public abstract RadioTreeItem getAVItem(GeoElement ob);

	public abstract RadioTreeItem getAVInput(Kernel kernel);

	public void setFontSize(int fontSize) {
		// TODO Auto-generated method stub

	}

	public boolean supportsAV() {
		return true;
	}
}
