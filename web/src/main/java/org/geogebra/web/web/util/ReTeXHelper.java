package org.geogebra.web.web.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASLaTeXEditor;
import org.geogebra.web.web.cas.view.CASTableControllerW;
import org.geogebra.web.web.cas.view.CASTableW;
import org.geogebra.web.web.gui.view.algebra.LatexTreeItem;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;

/**
 * Factory class for ReTeX based editor
 */
public class ReTeXHelper extends LaTeXHelper {

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public CASTableCellEditor getCASEditor(CASTableW table, AppW app,
			CASTableControllerW ml) {
		return new CASLaTeXEditor(table, app, ml);
	}

	@Override
	public RadioTreeItem getAVItem(GeoElement ob) {
		return new LatexTreeItem(ob);
	}

	@Override
	public RadioTreeItem getAVInput(Kernel kernel) {
		return new LatexTreeItem(kernel);
	}

	@Override
	public boolean supportsAV() {
		return false;
	}

}
