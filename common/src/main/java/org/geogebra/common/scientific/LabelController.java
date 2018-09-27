package org.geogebra.common.scientific;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;

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
		updateLabel(element, false);
	}

	/**
	 * Shows the label of the element.
	 *
	 * @param element the element
	 */
	public void showLabel(GeoElement element) {
		updateLabel(element, true);
	}

	private void updateLabel(GeoElement element, boolean show) {
		element.setAlgebraLabelVisible(show);
		LabelManager.getInstance().setLabel(show ? null : PREFIX, element);
		element.setDescriptionNeedsUpdateInAV(true);
		element.getKernel().notifyUpdate(element);
	}
}
