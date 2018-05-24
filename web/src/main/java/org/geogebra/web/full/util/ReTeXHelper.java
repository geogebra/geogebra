package org.geogebra.web.full.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.cas.view.CASLaTeXEditor;
import org.geogebra.web.full.cas.view.CASTableControllerW;
import org.geogebra.web.full.gui.view.algebra.CheckboxTreeItem;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.gui.view.algebra.SliderTreeItemRetex;
import org.geogebra.web.full.gui.view.algebra.TextTreeItem;
import org.geogebra.web.html5.main.AppW;

/**
 * Factory class for ReTeX based editor
 */
public class ReTeXHelper implements LaTeXHelper {

	@Override
	public CASTableCellEditor getCASEditor(AppW app,
			CASTableControllerW ml) {
		return new CASLaTeXEditor(app, ml);
	}

	@Override
	public RadioTreeItem getAVItem(GeoElement ob) {
		return new RadioTreeItem(ob);
	}

	@Override
	public RadioTreeItem getAVInput(Kernel kernel) {
		return new RadioTreeItem(kernel).initInput();
	}

	@Override
	public RadioTreeItem getSliderItem(GeoElement ob) {
		return new SliderTreeItemRetex(ob);
	}

	@Override
	public RadioTreeItem getCheckboxItem(GeoElement ob) {
		return new CheckboxTreeItem(ob);
	}

	@Override
	public RadioTreeItem getTextItem(GeoElement ob) {
		return new TextTreeItem(ob);
	}

}
