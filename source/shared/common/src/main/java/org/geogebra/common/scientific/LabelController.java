package org.geogebra.common.scientific;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;

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
	public boolean hasLabel(GeoElementND element) {
		return element.isLabelSet() && element.isAlgebraLabelVisible();
	}

	/**
	 * Hides the label of the element.
	 *
	 * @param element the element
	 */
	public void hideLabel(GeoElementND element) {
		updateLabel(element, false);
	}

	/**
	 * Shows the label of the element.
	 *
	 * @param element the element
	 */
	public void showLabel(GeoElementND element) {
		updateLabel(element, true);
	}

	private static void updateLabel(GeoElementND element, boolean show) {
		String label = element.getFreeLabel(show ? null : LabelManager.HIDDEN_PREFIX);
		element.setAlgebraLabelVisible(show);
		element.setLabel(label);
		ExpressionNode definition = element.getDefinition();
		if (definition != null) {
			definition.setLabel(label);
		}
		element.getKernel().notifyUpdate(element.toGeoElement());
	}

	/**
	 * @param element
	 *            construction element
	 */
	public void ensureHasLabel(GeoElementND element) {
		if (element != null && !hasLabel(element)) {
			showLabel(element);
		}
	}

	/**
	 * Ensures that table column has a label without algebra label visibility.
	 *
	 * @param element
	 *            construction element
	 */
	public void ensureHasLabelNoAlgebra(GeoElementND element) {
		if (element != null && !element.isLabelSet()) {
			showLabel(element);
		}
	}
}
