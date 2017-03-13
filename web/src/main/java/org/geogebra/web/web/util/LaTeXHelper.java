package org.geogebra.web.web.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASTableControllerW;
import org.geogebra.web.web.cas.view.CASTableW;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;

/**
 * Abstract factory for LaTeX related stuff
 *
 */
public abstract class LaTeXHelper {

	/**
	 * @param table
	 *            CAS table
	 * @param app
	 *            application
	 * @param ml
	 *            controller
	 * @return CAS editor
	 */
	public abstract CASTableCellEditor getCASEditor(CASTableW table, AppW app,
			CASTableControllerW ml);

	/**
	 * @param ob
	 *            element
	 * @return AV item for element
	 */
	public abstract RadioTreeItem getAVItem(GeoElement ob);

	/**
	 * @param kernel
	 *            kernel
	 * @return AV item for input
	 */
	public abstract RadioTreeItem getAVInput(Kernel kernel);

	/**
	 * @param fontSize
	 *            font size
	 */
	public void setFontSize(int fontSize) {
		// MQ only
	}

	/**
	 * @return whether input in AV is supported
	 */
	public boolean supportsAV() {
		return true;
	}

	/**
	 * @param ob
	 *            slider geo
	 * @return slider item
	 */
	public abstract RadioTreeItem getSliderItem(GeoElement ob);

	/**
	 * @param ob
	 *            bool
	 * @return chackbox item
	 */
	public abstract RadioTreeItem getCheckboxItem(GeoElement ob);
}
