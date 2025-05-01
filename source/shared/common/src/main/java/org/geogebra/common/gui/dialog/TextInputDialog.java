package org.geogebra.common.gui.dialog;

import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Text input dialog.
 */
public interface TextInputDialog {

	/**
	 * @param text text object
	 * @param startPoint position
	 * @param rw whether to use RW coordinates
	 */
	void reInitEditor(GeoText text, GeoPointND startPoint, boolean rw);

	/**
	 * Show or hide the dialog/
	 * @param b true to make it visible
	 */
	void setVisible(boolean b);
}
