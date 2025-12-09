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

package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

/**
 * Node simplifier.
 */
public interface SimplifyNode {

	/**
	 *
	 * @param node to check
	 * @return if node could be simplified with this class.
	 */
	boolean isAccepted(ExpressionNode node);

	/**
	 * Apply the simplifier to the node.
	 *
	 * @param node to apply.
	 * @return the simplified node.
	 */
	ExpressionNode apply(ExpressionNode node);

	/**
	 *
	 * @return the name of the simplifier.
	 */
	default String name() {

		String className = getClass().toString();
		String[] tags = className.split("\\.");
		return tags.length > 0 ? tags[tags.length - 1] : "-";
	}
}
