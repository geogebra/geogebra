package org.geogebra.common.gui.inputfield;

import org.geogebra.common.kernel.geos.GeoElement;

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
