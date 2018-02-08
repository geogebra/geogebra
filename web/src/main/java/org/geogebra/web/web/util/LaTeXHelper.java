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
