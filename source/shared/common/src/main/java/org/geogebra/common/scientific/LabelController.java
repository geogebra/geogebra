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
