/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
