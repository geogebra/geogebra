package org.geogebra.web.web.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASTableControllerW;
import org.geogebra.web.web.cas.view.CASTableW;
import org.geogebra.web.web.gui.view.algebra.AVTreeItem;
import org.geogebra.web.web.gui.view.algebra.LatexTreeItem;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;

public class ReTeXHelper extends LaTeXHelper {

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public CASTableCellEditor getCASEditor(CASTableW table, AppW app,
			CASTableControllerW ml) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVTreeItem getAVItem(GeoElement ob) {
		return new LatexTreeItem(ob);
	}

	@Override
	public RadioTreeItem getAVInput(Kernel kernel) {
		return new LatexTreeItem(kernel);
	}

	public boolean supportsAV() {
		return false;
	}

}
