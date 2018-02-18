package org.geogebra.web.full.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.cas.view.CASTableControllerW;
import org.geogebra.web.full.cas.view.CASTableW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.html5.main.AppW;

/**
 * Abstract factory for LaTeX related stuff
 *
 */
public interface LaTeXHelper {

	/**
	 * @param table
	 *            CAS table
	 * @param app
	 *            application
	 * @param ml
	 *            controller
	 * @return CAS editor
	 */
	CASTableCellEditor getCASEditor(CASTableW table, AppW app,
			CASTableControllerW ml);

	/**
	 * @param ob
	 *            element
	 * @return AV item for element
	 */
	RadioTreeItem getAVItem(GeoElement ob);

	/**
	 * @param kernel
	 *            kernel
	 * @return AV item for input
	 */
	RadioTreeItem getAVInput(Kernel kernel);

	/**
	 * @param ob
	 *            slider geo
	 * @return slider item
	 */
	RadioTreeItem getSliderItem(GeoElement ob);

	/**
	 * @param ob
	 *            bool
	 * @return checkbox item
	 */
	RadioTreeItem getCheckboxItem(GeoElement ob);

	/**
	 * @param ob
	 *            geo text
	 * @return text item
	 */
	RadioTreeItem getTextItem(GeoElement ob);
}
