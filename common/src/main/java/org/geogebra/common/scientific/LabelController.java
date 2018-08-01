package org.geogebra.common.scientific;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Handles showing and hiding the label for the Scientific Calculator
 */
public class LabelController {

	private static final String PREFIX = "\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA";

	/**
	 * Hides the label of the element.
	 *
	 * @param element the element
	 */
	public void hideLabel(GeoElement element) {
		element.setLabelVisible(false);
		element.setLabel(element.getFreeLabel(PREFIX));
		element.setDescriptionNeedsUpdateInAV(true);
		updateElement(element);
	}

	/**
	 * Shows the label of the element.
	 *
	 * @param element the element
	 */
	public void showLabel(GeoElement element) {
		element.setLabelVisible(true);
		element.setLabel(element.getFreeLabel(null));
		element.setDescriptionNeedsUpdateInAV(true);
		updateElement(element);
	}

	private void updateElement(GeoElement element) {
		element.getKernel().notifyUpdate(element);
	}
}
