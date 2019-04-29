package org.geogebra.common.scientific;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;

/**
 * Handles showing and hiding the label for the Scientific Calculator
 */
public class LabelController {

	/**
	 * Return true if the element has a label.
	 *
	 * @param element the element
	 * @return true if it has label
	 */
	public boolean hasLabel(GeoElement element) {
		String label = element.getLabelSimple();
		return label != null && !label.startsWith(LabelManager.HIDDEN_PREFIX);
	}

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

	private static void updateLabel(GeoElement element, boolean show) {
		String label = element.getFreeLabel(show ? null : LabelManager.HIDDEN_PREFIX);
		element.setAlgebraLabelVisible(show);
		element.setLabel(label);
		ExpressionNode definition = element.getDefinition();
		if (definition != null) {
			definition.setLabel(label);
		}
		element.setDescriptionNeedsUpdateInAV(true);
		element.getKernel().notifyUpdate(element);
	}
}
