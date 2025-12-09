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

package org.geogebra.common.gui.inputfield;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Provider of last items for the ANS functionality.
 */
public interface HasLastItem {

	/**
	 * @param element The GeoElement of the current AV item.
	 * @return Last output as string.
	 */
	String getPreviousItemFrom(GeoElement element);

	/**
	 * @return True if the last item is a simple number, otherwise false.
	 */
	boolean isLastItemSimpleNumber();

	/**
	 * @return True if the last item is a GeoText, otherwise false.
	 */
	boolean isLastItemText();
}
